/*
 * Project Name: workbench
 * File Name: BaseEnum.java
 * Class Name: BaseEnum
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

package com.hengtiansoft.bluemorpho.workbench.enums;

/**
 * 
 * 
 * @author SC
 * 
 */

public interface PageEnum {

    /**
     * Description: get the text for display
     *
     * @return
     */
    String getText();

    /**
     * Description: set the text for display
     *
     * @param text
     */
    void setText(String text);

    /**
     * Description: get the code of the enum
     *
     * @return
     */
    String getCode();

    /**
     * Description: set the code of the enum
     *
     * @param code
     */
    void setCode(String code);

    /**
     * Description: set the name of the enum
     *
     * @return
     */
    String name();

}
