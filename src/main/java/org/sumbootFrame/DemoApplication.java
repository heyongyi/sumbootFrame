package org.sumbootFrame;


import com.rpc.netty.server.DefaultServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetSocketAddress;

@EnableTransactionManagement
@SpringBootApplication
@ServletComponentScan
public class DemoApplication extends SpringBootServletInitializer implements CommandLineRunner {
	@Autowired
	private DefaultServer defaultServer;
	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);

//		SpringApplication app = new SpringApplication(DemoApplication.class);
//		app.setWebEnvironment(false);
//		app.run(args);
	}
	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(DemoApplication.class);
	}

	@Override
	public void run(String... strings) throws Exception {
		InetSocketAddress address = new InetSocketAddress("localhost", 9090);
		ChannelFuture future = defaultServer.start(address);

		Runtime.getRuntime().addShutdownHook(new Thread(){//jvm关闭钩子
			@Override
			public void run() {
				defaultServer.destroy();
			}
		});

		future.channel().closeFuture().syncUninterruptibly();
	}
}
