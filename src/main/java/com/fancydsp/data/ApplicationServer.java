package com.fancydsp.data;

import com.alibaba.druid.support.json.JSONUtils;
import com.fancydsp.data.domain.ResponseMessage;
import com.fancydsp.data.web.InnerErrorController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * ApplicationServer
 *
 */

@ServletComponentScan(value = "com.fancydsp.data.configuration")
@SpringBootApplication
public class ApplicationServer
{


    public static void main( String[] args )
    {

        SpringApplication.run(ApplicationServer.class, args);
    }
//    @Bean
//    RequestMappingHandlerMapping getRequestMappingHandlerMapping(){
//
//        return new RequestMappingHandlerMapping(){
//
//            @Nullable
//            private RequestMappingInfo myCreateRequestMappingInfo(AnnotatedElement element) {
//                RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
//                if(requestMapping == null && element instanceof Method){
//                    String name = ((Method) element).getName();
//                     RequestMappingInfo.Builder builder = RequestMappingInfo
//                            .paths("/"+name);
//                     return builder.build();
//
//                }else {
//                    RequestCondition<?> condition = (element instanceof Class ?
//                            getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
//
//                    return createRequestMappingInfo(requestMapping, condition);
//                }
//
//            }
//
//
//
//            @Override
//            @Nullable
//            protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
//                RequestMappingInfo info = myCreateRequestMappingInfo(method);
//                if (info != null) {
//                    RequestMappingInfo typeInfo = myCreateRequestMappingInfo(handlerType);
//                    if (typeInfo != null) {
//                        info = typeInfo.combine(info);
//                    }
//                }
//                return info;
//            }
//
//        };
//    }

    @Bean
    WebSecurityConfigurerAdapter getWebSecurityConfigurerAdapter(){
        return  new WebSecurityConfigurerAdapter(){
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.csrf().disable();
                http.authorizeRequests()
                        .antMatchers("/data/**").permitAll()
                        .antMatchers("/jobs/**").permitAll()
                        .antMatchers("/presto/**").permitAll()
                        .anyRequest().authenticated().mvcMatchers("/user/*").permitAll() //allow all user to acces// s
                        .and()
                        .formLogin()
                        .failureHandler(new AuthenticationFailureHandler() {
                            @Override
                            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("message","认证失败");
                                map.put("code",401);
                                response.getWriter().write(JSONUtils.toJSONString(map));
                                response.getWriter().flush();
                            }
                        })
                        .successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("message","success");
                                map.put("code",0);
                                response.getWriter().write(JSONUtils.toJSONString(map));
                                response.getWriter().flush();
                            }
                        });
               //
            }
        };
    }


    //JSON序列化控制
    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter(){
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(){
            @Override
            protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

                if(object instanceof InnerErrorController.ErrorResponse){
                    super.writeInternal(object, type, outputMessage);
                }else if(outputMessage instanceof Map){
                        super.writeInternal(object, type, outputMessage);
                }else {
                        int code = 0;
                        if (object instanceof ResponseMessage) {
                            code = ((ResponseMessage) object).getCode();
                        }
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("data", object);
                        map.put("code", code);
                        super.writeInternal(object, type, outputMessage);

                }

            }
        };
        return converter;
    }

}
