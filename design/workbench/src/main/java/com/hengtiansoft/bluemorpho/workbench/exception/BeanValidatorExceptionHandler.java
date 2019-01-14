package com.hengtiansoft.bluemorpho.workbench.exception;

/*
 * Project Name: workbench
 * File Name: ExceptionHandler.java
 * Class Name: ExceptionHandler
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

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;

import com.hengtiansoft.bluemorpho.workbench.dto.ResultDto;
import com.hengtiansoft.bluemorpho.workbench.dto.ValidationResultDto;
import com.hengtiansoft.bluemorpho.workbench.constant.ApplicationConstant;

/**
 * Class Name: ExceptionHandler
 * <p>
 * Description: the <code>ValidateException</code> handler<br>
 * the validation from service will be wrapped into <code>ValidateException</code>, then the handler will catch the
 * exception and return the errors into view
 * 
 * @author SC
 * 
 */
@Service
public class BeanValidatorExceptionHandler extends AbstractExceptionHandler {

    /**
     * 
     * Description: set the validation data.
     * 
     * @param constraintsViolatioins
     * @param handler
     * @param formId
     * @param error
     */
    @Override
    protected void setValidationErrorData(final Exception ex, final Object handler, final String formId,
            ResultDto<List<ValidationResultDto>> error) {
        ConstraintViolationException vex = (ConstraintViolationException) ex;
        Set<ConstraintViolation<?>> constraintsViolatioins = vex.getConstraintViolations();
        final List<ValidationResultDto> errorData = error.getData();
        if (StringUtils.isNotEmpty(formId) && constraintsViolatioins != null && constraintsViolatioins.size() > 0
                && handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // method parameter arrays
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            if (methodParameters != null && methodParameters.length > 0
                    && !ApplicationConstant.MANUAL_VALIDATE.equals(vex.getMessage())) {
                for (ConstraintViolation<?> constraintViolation : constraintsViolatioins) {
                    Class<?> doaminClass = constraintViolation.getRootBeanClass();
                    for (MethodParameter methodParameter : methodParameters) {
                        Class<?> dtoClass = methodParameter.getParameterType();
                        if (!dtoClass.equals(doaminClass)) {
                            continue;
                        } else if (doaminClass.equals(dtoClass)) {
                            setResultDto(constraintViolation, errorData, formId, false);
                        }
                    }
                }
            } else {
                for (ConstraintViolation<?> constraintViolation : constraintsViolatioins) {
                    setResultDto(constraintViolation, errorData, formId, true);
                }
            }
        }
    }

    /**
     * 
     * Description: set the result dto
     * 
     * @param constraintViolation
     * @throws NoSuchFieldException
     */
    private void setResultDto(ConstraintViolation<?> constraintViolation, List<ValidationResultDto> errorData,
            String formId, boolean notManually) {
        final String beanName = constraintViolation.getRootBeanClass().getName();
        final String errorMessage = constraintViolation.getMessage();
        final String fieldName = constraintViolation.getPropertyPath().toString();
        Class<?> rootClass = constraintViolation.getRootBeanClass();
        setFieldErrorMap(fieldName, beanName, rootClass, errorData, errorMessage, formId, notManually);
    }
}
