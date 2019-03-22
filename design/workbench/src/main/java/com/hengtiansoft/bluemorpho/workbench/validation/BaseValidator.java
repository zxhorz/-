/*
 * Project Name: workbench
 * File Name: BaseValidator.java
 * Class Name: BaseValidator
 *
 * Copyright 2014 Hengtian Software Inc
 *
 * Licensed under the Hengtiansoft
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

import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * 
 * @author SC
 * 
 */

public class BaseValidator {

    /**
     * 
     * Description: TODO
     * 
     * @param context
     * @param fieldName
     * @param messageTemplate
     */
    @SuppressWarnings("deprecation")
    protected void bindNode(final ConstraintValidatorContext context, final String fieldName, String messageTemplate) {
        String template = messageTemplate;
        if (StringUtils.isBlank(messageTemplate)) {
            template = context.getDefaultConstraintMessageTemplate();
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(template).addNode(fieldName).addConstraintViolation();
    }
}
