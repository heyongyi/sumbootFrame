package org.sumbootFrame.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

public final class PojoUtil {

    public static boolean containsKeys( Map<String, Object>map,String[] urlKeys){
        boolean ret=true;
        for (int i = 0 ,max = urlKeys.length;i<max;i++){
            if(StringUtils.isEmpty(map.get(urlKeys[i]))){
                ret=false;
                break;
            }
        }
        return ret;
    }
	public static Map<String, Object> getMapFromPojo(Object bean)
			throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();
		for (Method method : bean.getClass().getDeclaredMethods()) {
			if (Modifier.isPublic(method.getModifiers())
					&& method.getParameterTypes().length == 0
					&& method.getReturnType() != void.class
					&& method.getName().matches("^(get|is).+")) {
				String name = method.getName().replaceAll("^(get|is)", "");
				name = Character.toLowerCase(name.charAt(0))
						+ (name.length() > 1 ? name.substring(1) : "");
				Object value = method.invoke(bean);
				properties.put(name, value);
			}
		}
		return properties;
	}
	/**
	 * 复制对象属性排除哪些属性
	 * @param src
	 * @param dest
	 * @param exclude 排除属性列表
	 */
	public static void copyPropertiesExclude(Object src, Object dest, String[] exclude) throws Exception {
		Field[] sourceFields = src.getClass().getDeclaredFields();
		Field[] targetFields = dest.getClass().getDeclaredFields();
		for(Field sourceField : sourceFields){
			String name = sourceField.getName();
			Class type = sourceField.getType();
			if(exclude != null && !Arrays.asList(exclude).contains(name)){
				String methodName = name.substring(0, 1).toUpperCase() + name.substring(1);
				Method getMethod = src.getClass().getMethod("get" + methodName);
				Object value = getMethod.invoke(src);
				for(Field targetField : targetFields){
					String targetName = targetField.getName();
					if(targetName.equals(name)){
						Method setMethod = dest.getClass().getMethod("set" + methodName, type);
						setMethod.invoke(dest, value);
					}
				}
			}
		}
	}

	/**
	 * 复制对象中哪些属性
	 * @param src
	 * @param dest
	 * @param include
	 */
	public static void copyPropertiesInclude(Object src, Object dest, String[] include) throws Exception {
		Field[] sourceFields = src.getClass().getDeclaredFields();
		Field[] targetFields = dest.getClass().getDeclaredFields();
		for(Field sourceField : sourceFields){
			String name = sourceField.getName();
			Class type = sourceField.getType();
			if(include != null && Arrays.asList(include).contains(name)){
				String methodName = name.substring(0, 1).toUpperCase() + name.substring(1);
				Method getMethod = src.getClass().getMethod("get" + methodName);
				Object value = getMethod.invoke(src);
				for(Field targetField : targetFields){
					String targetName = targetField.getName();
					if(targetName.equals(name)){
						Method setMethod = dest.getClass().getMethod("set" + methodName, type);
						setMethod.invoke(dest, value);
					}
				}
			}
		}
	}



	/**
	 * 判断方法数组中是否存在某方法
	 *
	 * @param methods
	 * @param name
	 * @return method
	 */
	private static Method findMethodByName(Method[] methods, String name) {
		Method method = null;
		for (int i = 0,max=methods.length; i < max; i++) {
			if (methods[i].getName().equals(name)){
				method =  methods[i];
			}
		}
		return method;
	}

	public static void copyProperties(Object src, Object dest) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {

        Date defaultValue = null;
        Converter converter = new DateConverter(defaultValue);
        BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
        beanUtilsBean.getConvertUtils().register(converter, Date.class);
        BeanUtils.copyProperties(dest, src);
    }
	public static Object getPojoFromMap(Class<?> cls,HashMap<String, Object> map)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SecurityException, ParseException {
		return getPojoFromMap(cls,map,"");
	}
	public static Object getPojoFromMap(Class<?> cls,
			HashMap<String, Object> map, String prefix)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SecurityException, ParseException {
		Object pojo = null;
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null
					&& entry.getValue().toString().length() != 0) {
				try {
					String fieldName = entry.getKey();
					if (prefix != null && prefix.length() > 0) {
						if (fieldName.startsWith(prefix)) {
							fieldName = fieldName.replaceFirst(prefix, "");
						} else {
							continue;
						}
					}
					Field field = cls.getDeclaredField(fieldName);
					field.setAccessible(true);
					field.getType();

					if (pojo == null) {
						pojo = cls.newInstance();
					}
					if (field.getType() == Date.class) {
						if (entry.getValue() instanceof Date) {
							field.set(pojo, entry.getValue());
						}
						if (entry.getValue() instanceof String) {
							field.set(pojo, DateUtil.parse((String) entry.getValue()));
						}
					} else if (field.getType() == Long.class||field.getType()== long.class) {
						 if (entry.getValue() instanceof Long) {
							field.set(pojo,  ((Long) entry.getValue()).longValue());
						} else if (entry.getValue() instanceof Integer) {
							field.set(pojo, ((Integer) entry.getValue()).longValue());
						} else if (entry.getValue() instanceof BigDecimal) {
							field.set(pojo, ((BigDecimal) entry.getValue()).longValue());
						}else{
							 field.set(pojo, Integer.parseInt((String) entry.getValue()));
						 }
					} else if (field.getType() == Integer.class||field.getType()== int.class) {
						if (entry.getValue() instanceof Long) {
							field.set(pojo,  ((Long) entry.getValue()).intValue());
						} else if (entry.getValue() instanceof Integer) {
							field.set(pojo, ((Integer) entry.getValue()).intValue());
						} else if (entry.getValue() instanceof BigDecimal) {
							field.set(pojo, ((BigDecimal) entry.getValue()).intValue());
						}else{
							field.set(pojo, Integer.parseInt((String) entry.getValue()));
						}
					} else if (field.getType() == BigDecimal.class) {
						field.set(pojo, new BigDecimal((String) entry.getValue()));
					}else if (field.getType() == Double.class) {
						if (entry.getValue() instanceof Double) {
							field.set(pojo, entry.getValue());
						}else{
							field.set(pojo, Double.parseDouble((String) entry.getValue()));
						}
					}else if (field.getType() == Float.class) {
						if (entry.getValue() instanceof Float) {
							field.set(pojo, entry.getValue());
						}else{
							field.set(pojo, Float.parseFloat((String) entry.getValue()));
						}
					}else if (field.getType() == String.class) {
						if (entry.getValue() instanceof Long) {
							field.set(pojo,Long.toString((Long) entry.getValue()));
						} else if (entry.getValue() instanceof Integer) {
							field.set(pojo, Integer.toString((Integer) entry
									.getValue()));
						} else {
							field.set(pojo, entry.getValue());
						}
					} else {
						field.set(pojo, entry.getValue());
					}
				} catch (NoSuchFieldException e) {
					continue;
				}
			}
		}
		return pojo;
	}
	public static String JabxPojoToXml(Object obj) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		jaxbMarshaller.setProperty("com.sun.xml.bind.xmlHeaders",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		ByteArrayOutputStream osxml = new ByteArrayOutputStream();
		jaxbMarshaller.marshal(obj, osxml);
		return osxml.toString();
	}
	public static Object JabxXmlToPojo(Class clazz, String xmlText)
			throws JAXBException {
		JAXBContext jaxbContextResponse = JAXBContext.newInstance(clazz);
		Unmarshaller jaxbUnmarshaller = jaxbContextResponse
				.createUnmarshaller();
		return jaxbUnmarshaller.unmarshal(new StringReader(xmlText));
	}

	public static String toJson(Object obj){
		ObjectMapper mapper = new ObjectMapper();
        String Json=null;
        try {
            Json =  mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Json;
    }

    public static Object jsonToPojo(String json,Class<?> clazz ){
        ObjectMapper mapper = new ObjectMapper();
        Object obj=null;
        try {
            obj = (Object)mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static List<Object> jsonToPojoList(String json, Class<?>... elementClasses){
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, elementClasses);
        List<Object> lst=null;
        try {
            lst =  (List<Object>)mapper.readValue(json, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lst;
    }

    public static Map<String,Object> jsonToPojoMap(String json, Class<?> cls){
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(HashMap.class,String.class,cls );
        Map<String,Object> map=null;
        try {
            map = (Map<String,Object>)mapper.readValue(json, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

	/**
	 * 将Json对象字符串转化为List<Map>对象
	 * @param jsonStr JSON字符串
	 * @return 转换成功返回Map对象，失败则返回null
	 */
	public static List<HashMap<String, Object>> jsonToMapList(String jsonStr) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			List<HashMap<String, Object>> params = objectMapper.readValue(jsonStr,List.class);
			for (int i = 0; i < params.size(); i++) {
				Map<String, Object> map = params.get(i);
				Set<String> set = map.keySet();
				for (Iterator<String> it = set.iterator();it.hasNext();) {
					String key = it.next();
				}
			}
			return params;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
     * 对象转数组
     * @param obj
     * @return
     */
    public static byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     * @param bytes
     * @return
     */
    public static Object toObject (byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

}
