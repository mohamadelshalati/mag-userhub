package com.mag.app.exceptions;

import com.mag.app.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getFieldErrors().forEach(fieldError -> {
            String message = Objects.requireNonNullElse(
                    fieldError.getDefaultMessage(),
                    fieldError.getField() + " is invalid"
            );

            errors.merge(fieldError.getField(), message, (existing, incoming) -> existing + "; " + incoming);
        });

        ApiError apiError = ApiError.of(
                request.getMethod(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(request.getMethod(), HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ApiError apiError = ApiError.of(
                request.getMethod(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        String userFriendlyMessage = "Error processing data";

        if (rootMessage != null) {
            if (rootMessage.toLowerCase().contains("duplicate key")) {
                userFriendlyMessage = "This record or a unique field (email) already exists.";
                ApiError apiError = ApiError.of(
                        request.getMethod(),
                        HttpStatus.UNPROCESSABLE_CONTENT.value(),
                        "Unprocessable Content",
                        userFriendlyMessage,
                        request.getRequestURI(),
                        Map.of("email","Email already exists")
                );
                return ResponseEntity.unprocessableContent().body(apiError);
            }
            else if (rootMessage.toLowerCase().contains("too long") || rootMessage.toLowerCase().contains("data truncation")) {
                userFriendlyMessage = "One of the provided fields exceeds the maximum character limit.";
                ApiError apiError = ApiError.of(
                        request.getMethod(),
                        HttpStatus.UNPROCESSABLE_CONTENT.value(),
                        "Unprocessable Content",
                        userFriendlyMessage,
                        request.getRequestURI()
                );
                return ResponseEntity.unprocessableContent().body(apiError);
            }
        }

        ApiError apiError = ApiError.of(
                request.getMethod(),
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                "Unprocessable Content",
                userFriendlyMessage,
                request.getRequestURI()
        );
        return ResponseEntity.unprocessableContent().body(apiError);
    }

}


//{
//        "timestamp": "2026-09-20T23:24:16.570Z",
//        "status": 500,
//        "error": "Internal Server Error",
//        "trace": "org.springframework.dao.DataIntegrityViolationException: could not execute statement [ERROR: duplicate key value violates unique constraint \"idx_users_email_lower\"\n  Detail: Key (lower(email::text))=(andree.flita@gmail.com) already exists.] [insert into users (city,country,date_created,email,first_name,last_name,profession,id) values (?,?,?,?,?,?,?,?)]; SQL [insert into users (city,country,date_created,email,first_name,last_name,profession,id) values (?,?,?,?,?,?,?,?)]; constraint [idx_users_email_lower]\n\tat org.springframework.orm.jpa.hibernate.HibernateExceptionTranslator.convertHibernateAccessException(HibernateExceptionTranslator.java:169)\n\tat org.springframework.orm.jpa.hibernate.HibernateExceptionTranslator.convertHibernateAccessException(HibernateExceptionTranslator.java:131)\n\tat org.springframework.orm.jpa.hibernate.HibernateExceptionTranslator.translateExceptionIfPossible(HibernateExceptionTranslator.java:105)\n\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:223)\n\tat org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:556)\n\tat org.springframework.transaction.support.AbstractPlatformTransactionManager.processCommit(AbstractPlatformTransactionManager.java:794)\n\tat org.springframework.transaction.support.AbstractPlatformTransactionManager.commit(AbstractPlatformTransactionManager.java:757)\n\tat org.springframework.transaction.interceptor.TransactionAspectSupport.commitTransactionAfterReturning(TransactionAspectSupport.java:687)\n\tat org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:408)\n\tat org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:130)\n\tat org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)\n\tat org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:135)\n\tat org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)\n\tat org.springframework.data.jpa.repository.support.CrudMethodMetadataPostProcessor$CrudMethodMetadataPopulatingMethodInterceptor.invoke(CrudMethodMetadataPostProcessor.java:166)\n\tat org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)\n\tat org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:222)\n\tat jdk.proxy4/jdk.proxy4.$Proxy138.save(Unknown Source)\n\tat com.mag.app.user.UserService.createUser(UserService.java:84)\n\tat com.mag.app.user.UserController.createUser(UserController.java:26)\n\tat java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:565)\n\tat org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:252)\n\tat org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:184)\n\tat org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:117)\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:934)\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:853)\n\tat org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:86)\n\tat org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:963)\n\tat org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:866)\n\tat org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1000)\n\tat org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:903)\n\tat jakarta.servlet.http.HttpServlet.service(HttpServlet.java:649)\n\tat org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:874)\n\tat jakarta.servlet.http.HttpServlet.service(HttpServlet.java:710)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:132)\n\tat org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:59)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111)\n\tat org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111)\n\tat org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111)\n\tat org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:199)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111)\n\tat org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:165)\n\tat org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:77)\n\tat org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:535)\n\tat org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115)\n\tat org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:86)\n\tat org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:71)\n\tat org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:347)\n\tat org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:406)\n\tat org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:71)\n\tat org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:1307)\n\tat org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:2036)\n\tat org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:74)\n\tat org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:949)\n\tat org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:483)\n\tat org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:74)\n\tat java.base/java.lang.Thread.run(Thread.java:1474)\nCaused by: org.hibernate.exception.ConstraintViolationException: could not execute statement [ERROR: duplicate key value violates unique constraint \"idx_users_email_lower\"\n  Detail: Key (lower(email::text))=(andree.flita@gmail.com) already exists.] [insert into users (city,country,date_created,email,first_name,last_name,profession,id) values (?,?,?,?,?,?,?,?)]\n\tat org.hibernate.exception.internal.SQLStateConversionDelegate.convert(SQLStateConversionDelegate.java:73)\n\tat org.hibernate.exception.internal.StandardSQLExceptionConverter.convert(StandardSQLExceptionConverter.java:34)\n\tat org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(SqlExceptionHelper.java:115)\n\tat org.hibernate.engine.jdbc.internal.ResultSetReturnImpl.executeUpdate(ResultSetReturnImpl.java:189)\n\tat org.hibernate.engine.jdbc.mutation.internal.AbstractMutationExecutor.performNonBatchedMutation(AbstractMutationExecutor.java:145)\n\tat org.hibernate.engine.jdbc.mutation.internal.MutationExecutorSingleNonBatched.performNonBatchedOperations(MutationExecutorSingleNonBatched.java:53)\n\tat org.hibernate.engine.jdbc.mutation.internal.AbstractMutationExecutor.execute(AbstractMutationExecutor.java:65)\n\tat org.hibernate.engine.jdbc.mutation.internal.AbstractMutationExecutor.execute(AbstractMutationExecutor.java:54)\n\tat org.hibernate.persister.entity.mutation.InsertCoordinatorStandard.doStaticInserts(InsertCoordinatorStandard.java:183)\n\tat org.hibernate.persister.entity.mutation.InsertCoordinatorStandard.coordinateInsert(InsertCoordinatorStandard.java:123)\n\tat org.hibernate.persister.entity.mutation.InsertCoordinatorStandard.insert(InsertCoordinatorStandard.java:96)\n\tat org.hibernate.action.internal.EntityInsertAction.execute(EntityInsertAction.java:108)\n\tat org.hibernate.engine.spi.ActionQueue.executeActions(ActionQueue.java:634)\n\tat org.hibernate.engine.spi.ActionQueue.executeActions(ActionQueue.java:505)\n\tat org.hibernate.event.internal.AbstractFlushingEventListener.performExecutions(AbstractFlushingEventListener.java:386)\n\tat org.hibernate.event.internal.DefaultFlushEventListener.onFlush(DefaultFlushEventListener.java:40)\n\tat org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:138)\n\tat org.hibernate.internal.SessionImpl.fireFlush(SessionImpl.java:1472)\n\tat org.hibernate.internal.SessionImpl.managedFlush(SessionImpl.java:498)\n\tat org.hibernate.internal.SessionImpl.flushBeforeTransactionCompletion(SessionImpl.java:2100)\n\tat org.hibernate.internal.SessionImpl.beforeTransactionCompletion(SessionImpl.java:2021)\n\tat org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl.beforeTransactionCompletion(JdbcCoordinatorImpl.java:426)\n\tat org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl.beforeCompletionCallback(JdbcResourceLocalTransactionCoordinatorImpl.java:167)\n\tat org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl$TransactionDriverControlImpl.commitNoRollbackOnly(JdbcResourceLocalTransactionCoordinatorImpl.java:252)\n\tat org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl$TransactionDriverControlImpl.commit(JdbcResourceLocalTransactionCoordinatorImpl.java:242)\n\tat org.hibernate.engine.transaction.internal.TransactionImpl.commit(TransactionImpl.java:89)\n\tat org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:552)\n\t... 57 more\nCaused by: org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"idx_users_email_lower\"\n  Detail: Key (lower(email::text))=(andree.flita@gmail.com) already exists.\n\tat org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(QueryExecutorImpl.java:2993)\n\tat org.postgresql.core.v3.QueryExecutorImpl.processResults(QueryExecutorImpl.java:2656)\n\tat org.postgresql.core.v3.QueryExecutorImpl.execute(QueryExecutorImpl.java:446)\n\tat org.postgresql.jdbc.PgStatement.executeInternal(PgStatement.java:533)\n\tat org.postgresql.jdbc.PgStatement.execute(PgStatement.java:449)\n\tat org.postgresql.jdbc.PgPreparedStatement.executeWithFlags(PgPreparedStatement.java:197)\n\tat org.postgresql.jdbc.PgPreparedStatement.executeUpdate(PgPreparedStatement.java:158)\n\tat com.zaxxer.hikari.pool.ProxyPreparedStatement.executeUpdate(ProxyPreparedStatement.java:61)\n\tat com.zaxxer.hikari.pool.HikariProxyPreparedStatement.executeUpdate(HikariProxyPreparedStatement.java)\n\tat org.hibernate.engine.jdbc.internal.ResultSetReturnImpl.executeUpdate(ResultSetReturnImpl.java:185)\n\t... 80 more\n",
//        "message": "could not execute statement [ERROR: duplicate key value violates unique constraint \"idx_users_email_lower\"\n  Detail: Key (lower(email::text))=(andree.flita@gmail.com) already exists.] [insert into users (city,country,date_created,email,first_name,last_name,profession,id) values (?,?,?,?,?,?,?,?)]; SQL [insert into users (city,country,date_created,email,first_name,last_name,profession,id) values (?,?,?,?,?,?,?,?)]; constraint [idx_users_email_lower]",
//        "path": "/api/users"
//        }