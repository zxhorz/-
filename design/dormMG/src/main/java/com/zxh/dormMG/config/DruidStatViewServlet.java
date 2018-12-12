/*
 * Project Name: springbootdemo
 * File Name: DruidStatViewServlet.java
 * Class Name: DruidStatViewServlet
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
package com.zxh.dormMG.config;

/**
* Class Name: DruidStatViewServlet
* Description: TODO
* @author jintaoxu
*
*/

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * StatViewServlet
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/druid/*",
    initParams={
            @WebInitParam(name="allow",value=""),// IP白名单 (没有配置或者为空，则允许所有访问)
           // @WebInitParam(name="deny",value=""),// IP黑名单 (存在共同时，deny优先于allow)
            @WebInitParam(name="loginUsername",value="root"),// 用户名
            @WebInitParam(name="loginPassword",value="root"),// 密码
            @WebInitParam(name="resetEnable",value="false")// 禁用HTML页面上的“Reset All”功能
    })
public class DruidStatViewServlet extends StatViewServlet {
    private static final long serialVersionUID = -2065571810188326159L;
}
