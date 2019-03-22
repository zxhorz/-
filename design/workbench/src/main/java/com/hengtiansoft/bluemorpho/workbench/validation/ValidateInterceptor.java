/*
 * Project Name: workbench
 * File Name: ValidateInterceptor.java
 * Class Name: ValidateInterceptor
 *
 * Copyright 2014 Hengtian Software Inc
 *
 * 
 *
 * http://www.hengtiansoft.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hengtiansoft.bluemorpho.workbench.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.hengtiansoft.bluemorpho.workbench.dto.annotation.OnValid;
import com.hengtiansoft.bluemorpho.workbench.util.ApplicationContextUtil;

/**
 * Class Name: ValidateInterceptor
 * <p>
 * Description: the Interceptor for service validation
 * 
 * @author SC
 * 
 */
@Component
@Aspect
public class ValidateInterceptor {

    @Autowired
    private LocalValidatorFactoryBean validator;

    public ValidateInterceptor() {
        if (validator == null) {
            validator = ApplicationContextUtil.getBean(LocalValidatorFactoryBean.class);
        }
    }

    private static final String EXECUTION = "execution(* com.hengtiansoft.bluemorpho.workbench..*.*(..,@com.hengtiansoft.bluemorpho.workbench.dto.annotation.OnValid (*),..))";

    /**
     * defination pointcut of service method stick <code>@Validated</code> in args.
     */
    @Pointcut(EXECUTION)
    public void findValidateAnnotation() {
    }

    /**
     * validate the business logic before executing target method.
     */
    @Before("findValidateAnnotation()")
    public void validate(final JoinPoint jp) throws ConstraintViolationException {
        final Signature signature = jp.getSignature();
        final Object[] args = jp.getArgs();
        final MethodSignature methodSignature = (MethodSignature) signature;
        final Method targetMethod = methodSignature.getMethod();
        Set<ConstraintViolation<?>> result = new HashSet<ConstraintViolation<?>>();
        final Annotation[][] paraAnnotations = targetMethod.getParameterAnnotations();// get the parameters annotations
        if (paraAnnotations != null && paraAnnotations.length > 0) {
            for (int i = 0; i < paraAnnotations.length; i++) {
                int paraAnnotationLength = paraAnnotations[i].length;// current parameter annotation length
                if (paraAnnotationLength == 0) { // no annotation on current parameter
                    continue;
                }
                for (int j = 0; j < paraAnnotationLength; j++) {
                    if (paraAnnotations[i][j] instanceof OnValid) {
                        OnValid validated = (OnValid) (paraAnnotations[i][j]);
                        Class<?>[] groups = (validated.value());
                        Object validatedObj = args[i];
                        executeValidate(validatedObj, groups, result);
                        break;
                    }
                }
            }
        }
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
    }

    /**
     * 
     * Description: execute the validate and set into result set.
     * 
     * @param validatedObj
     * @param groups
     * @param result
     */
    private void executeValidate(final Object validatedObj, final Class<?>[] groups,
            final Set<ConstraintViolation<?>> result) {
        final Set<ConstraintViolation<Object>> constraintViolations = validator.validate(validatedObj, groups);
        if (!constraintViolations.isEmpty()) {
            result.addAll(constraintViolations);
        }
    }

}
