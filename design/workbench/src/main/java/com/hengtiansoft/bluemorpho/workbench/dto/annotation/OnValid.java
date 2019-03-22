/*
 * Project Name: workbench
 * File Name: Validateded.java
 * Class Name: Validateded
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

package com.hengtiansoft.bluemorpho.workbench.dto.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 
 * @author SC
 * 
 */
@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnValid {

    /**
     * Specify one or more validation groups to apply to the validation step kicked off by this annotation.
     * <p>
     * JSR-303 defines validation groups as custom annotations which an application declares for the sole purpose of
     * using them as type-safe group arguments, as implemented in
     * {@link org.springframework.validation.beanvalidation.SpringValidatorAdapter}.
     * <p>
     * Other {@link org.springframework.validation.SmartValidator} implementations may support class arguments in other
     * ways as well.
     */
    Class<?>[] value() default {};
}
