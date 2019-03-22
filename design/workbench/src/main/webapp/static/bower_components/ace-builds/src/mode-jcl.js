define("ace/mode/jcl_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module) {
    "use strict";
    
    var oop = require("../lib/oop");
    var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;
    
    var JclHighlightRules = function() {
        var keywords = "JOB|JCLLIB|EXEC|DD|INCLUDE|STEPLIB|PROC|SET|DSN|DSNAME|JOBCAT|JOBLIB";
    var builtinConstants = (
        "true|false|null"
    );

    var builtinFunctions = (
        "count|min|max|avg|sum|rank|now|coalesce|main"
    );

    var keywordMapper = this.createKeywordMapper({
        "support.function": builtinFunctions,
        "keyword": keywords,
        "constant.language": builtinConstants
    }, "identifier", true);
    
    var sysInLineMapper = this.createKeywordMapper({
    	 "support.function": builtinFunctions,
         "keyword": keywords,
         "constant.language": builtinConstants
    }, "identifier", true);

     this.$rules = {
        "start" : [{
            token : keywordMapper,
            regex : "[a-zA-Z_$][a-zA-Z0-9-$]*\\b"
        }, {
            token : "keyword.operator",
            regex : "\\+|%|<@>|@>|<@|&|\\^|~|<|>|<=|=>|==|!=|<>|="
        },{
        	token : "comma",
        	regex : "[,]"
        }, {
            token : "comment",
            regex : "\\/\\/\\*.*$"
        }, {
            token : "string",           // " string
            regex : '".*?"'
        }, {
            token : "string",           // ' string
            regex : "'.*?'"
        }, {
            token : "paren.lparen",
            regex : "[\\(]"
        }, {
            token : "paren.rparen",
            regex : "[\\)]"
        },  {
        	token: "common",
        	regex: "\\/\\/SYSIN",
        	next: "sysinKey"
        }],
        "sysinKey" : [{
            token : keywordMapper,
            regex : "[a-zA-Z_$][a-zA-Z0-9-$]*\\b"
        }, {
            token : "common",
            regex : "\\*",
            next: "sysinEnd"
        }],
        "sysinEnd" : [{
        	token: "common",
        	regex: ".*\\/\\/",
        	next: "start"
        }, {
            token : "sysintext",
            regex : ".+"
        }]
    };
};
    
    oop.inherits(JclHighlightRules, TextHighlightRules);
    
    exports.JclHighlightRules = JclHighlightRules;
    });
    
    define("ace/mode/jcl",["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/jcl_highlight_rules"], function(require, exports, module) {
    "use strict";
    
    var oop = require("../lib/oop");
    var TextMode = require("./text").Mode;
    var JclHighlightRules = require("./jcl_highlight_rules").JclHighlightRules;
    
    var Mode = function() {
        this.HighlightRules = JclHighlightRules;
        this.$behaviour = this.$defaultBehaviour;
    };
    oop.inherits(Mode, TextMode);
    
    (function() {
    
        this.lineCommentStart = "*";
    
        this.$id = "ace/mode/jcl";
    }).call(Mode.prototype);
    
    exports.Mode = Mode;
    
    });
                    (function() {
                        window.require(["ace/mode/jcl"], function(m) {
                            if (typeof module == "object" && typeof exports == "object" && module) {
                                module.exports = m;
                            }
                        });
                    })();
                