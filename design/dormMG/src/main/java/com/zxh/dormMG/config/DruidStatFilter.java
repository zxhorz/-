package com.zxh.dormMG.config;
/*
 * Project Name: springbootdemo
 * File Name: DruidStatFilter.java
 * Class Name: DruidStatFilter
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


/**
 * Class Name: DruidStatFilter
 * Description: TODO
 *
 * @author jintaoxu
 */

import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * Druid的StatFilter
 */
@WebFilter(filterName = "druidWebStatFilter", urlPatterns = "/*",
        initParams = {
                @WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
        })
public class DruidStatFilter extends WebStatFilter {

}