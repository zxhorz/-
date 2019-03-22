define("ace/theme/cobol-mainframe",["require","exports","module","ace/lib/dom"], function(require, exports, module) {

exports.isDark = true;
exports.cssClass = "ace-cobol-mainframe";
exports.cssText = ".ace-cobol-mainframe .ace_gutter {\
background: #00060C;\
color: #24d830\
}\
.ace-cobol-mainframe {\
    background-color: #00060C;\
    color: #24d830;\
  }\
  .ace-cobol-mainframe .ace_gutter {\
    background: #2e3033;\
    color: #d0edf7;\
  }\
  .ace-cobol-mainframe .ace_print-margin {\
    width: 1px;\
    background: #33555e;\
  }\
  .ace-cobol-mainframe .ace_entity.ace_other.ace_attribute-name,\
  .ace-cobol-mainframe .ace_storage {\
    color: red;\
  }\
  .ace-cobol-mainframe .ace_cursor,\
  .ace-cobol-mainframe .ace_string.ace_regexp {\
    color: #d30102;\
  }\
  .ace-cobol-mainframe .ace_marker-layer .ace_active-line,\
  .ace-cobol-mainframe .ace_marker-layer .ace_selection {\
    background: rgba(0, 255, 255, 0.35);\
  }\
  .ace-cobol-mainframe .ace_marker-layer .ace_active-line {\
    background: none;\
  }\
  .ace-cobol-mainframe .ace_multiselect .ace_selection.ace_start {\
    box-shadow: 0 0 3px 0px #002B36;\
    border-radius: 2px;\
  }\
  .ace-cobol-mainframe .ace_marker-layer .ace_step {\
    background: #665200;\
  }\
  .ace-cobol-mainframe .ace_marker-layer .ace_bracket {\
    margin: -1px 0 0 -1px;\
    border: 1px solid rgba(147, 161, 161, 0.5);\
  }\
  .ace-cobol-mainframe .ace_gutter-active-line {\
    background-color: #0d3440;\
    background: #000;\
  }\
  .ace-cobol-mainframe .ace_marker-layer .ace_selected-word {\
    border: 1px solid #073642;\
  }\
  .ace-cobol-mainframe .ace_invisible {\
    color: rgba(147, 161, 161, 0.5);\
  }\
  .ace-cobol-mainframe .ace_keyword,\
  .ace-cobol-mainframe .ace_meta,\
  .ace-cobol-mainframe .ace_support.ace_class,\
  .ace-cobol-mainframe .ace_support.ace_type {\
    color: #f01818;\
  }\
  .ace-cobol-mainframe .ace_constant.ace_character,\
  .ace-cobol-mainframe .ace_constant.ace_other {\
    color: #cb4b16;\
  }\
  .ace-cobol-mainframe .ace_constant.ace_language {\
    color: #b58900;\
  }\
  .ace-cobol-mainframe .ace_constant.ace_numeric {\
    color: #24d830;\
  }\
  .ace-cobol-mainframe .ace_fold {\
    background-color: #268BD2;\
    border-color: #93a1a1;\
  }\
  .ace-cobol-mainframe .ace_entity.ace_name.ace_function,\
  .ace-cobol-mainframe .ace_entity.ace_name.ace_tag,\
  .ace-cobol-mainframe .ace_support.ace_function,\
  .ace-cobol-mainframe .ace_variable,\
  .ace-cobol-mainframe .ace_variable.ace_language {\
    color: #d0edf7;\
  }\
  .ace-cobol-mainframe .ace_string {\
    color: #ffffff;\
  }\
  .ace-cobol-mainframe .ace_comment {\
    font-style: italic;\
    color: #58f0f0;\
  }\
  .ace-cobol-mainframe .ace_dot {\
    font-style: italic;\
    color: #FFFF00;\
  }\
  .ace-cobol-mainframe .ace_sql {\
    font-style: italic;\
    color: #7890f0;\
  }\
  .ace-cobol-mainframe .ace_page {\
    color: #7890f0;\
  }\
  .ace-cobol-mainframe .ace_include {\
    color: #7890f0;\
  }\
  .ace-cobol-mainframe .ace_indent-guide {\
    background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAEklEQVQImWNg0Db1ZVCxc/sPAAd4AlUHlLenAAAAAElFTkSuQmCC) right repeat-y;\
  }";
var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass);
});
                (function() {
                    window.require(["ace/theme/cobol-mainframe"], function(m) {
                        if (typeof module == "object" && typeof exports == "object" && module) {
                            module.exports = m;
                        }
                    });
                })();
            