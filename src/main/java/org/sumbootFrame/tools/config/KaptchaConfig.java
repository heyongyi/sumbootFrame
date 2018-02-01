package org.sumbootFrame.tools.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by thinkpad on 2017/9/12.
 */

@ConfigurationProperties(prefix = "sum.kaptcha")
@PropertySource({
        "classpath:properties/sum.properties",
        "classpath:self-properties/sum-self.properties"
})
@Component
public class KaptchaConfig {
    private String border ;
    private String bordercolor ;
    private String imagewidth ;
    private String imageheight ;
    private String fontsize ;
    private String textproducercharlength ;
    private String textproducerfontcolor ;
    private String textproducercharspace ;
    private String textproducercharstring ;
    private String textproducerfontnames;
    private String backgroundclearfrom ;
    private String backgroundclearto ;
    private String noiseimpl ;
    private String obscurificatorimpl ;
    private String noisecolor ;


    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public String getBordercolor() {
        return bordercolor;
    }

    public void setBordercolor(String bordercolor) {
        this.bordercolor = bordercolor;
    }

    public String getImagewidth() {
        return imagewidth;
    }

    public void setImagewidth(String imagewidth) {
        this.imagewidth = imagewidth;
    }

    public String getImageheight() {
        return imageheight;
    }

    public void setImageheight(String imageheight) {
        this.imageheight = imageheight;
    }

    public String getFontsize() {
        return fontsize;
    }

    public void setFontsize(String fontsize) {
        this.fontsize = fontsize;
    }

    public String getTextproducercharlength() {
        return textproducercharlength;
    }

    public void setTextproducercharlength(String textproducercharlength) {
        this.textproducercharlength = textproducercharlength;
    }

    public String getTextproducerfontcolor() {
        return textproducerfontcolor;
    }

    public void setTextproducerfontcolor(String textproducerfontcolor) {
        this.textproducerfontcolor = textproducerfontcolor;
    }

    public String getTextproducercharspace() {
        return textproducercharspace;
    }

    public void setTextproducercharspace(String textproducercharspace) {
        this.textproducercharspace = textproducercharspace;
    }

    public String getTextproducercharstring() {
        return textproducercharstring;
    }

    public void setTextproducercharstring(String textproducercharstring) {
        this.textproducercharstring = textproducercharstring;
    }

    public String getBackgroundclearfrom() {
        return backgroundclearfrom;
    }

    public void setBackgroundclearfrom(String backgroundclearfrom) {
        this.backgroundclearfrom = backgroundclearfrom;
    }

    public String getBackgroundclearto() {
        return backgroundclearto;
    }

    public void setBackgroundclearto(String backgroundclearto) {
        this.backgroundclearto = backgroundclearto;
    }

    public String getNoiseimpl() {
        return noiseimpl;
    }

    public void setNoiseimpl(String noiseimpl) {
        this.noiseimpl = noiseimpl;
    }

    public String getObscurificatorimpl() {
        return obscurificatorimpl;
    }

    public void setObscurificatorimpl(String obscurificatorimpl) {
        this.obscurificatorimpl = obscurificatorimpl;
    }

    public String getNoisecolor() {
        return noisecolor;
    }

    public void setNoisecolor(String noisecolor) {
        this.noisecolor = noisecolor;
    }

    public String getTextproducerfontnames() {
        return textproducerfontnames;
    }

    public void setTextproducerfontnames(String textproducerfontnames) {
        this.textproducerfontnames = textproducerfontnames;
    }
}
