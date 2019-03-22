define("ace/mode/cobol_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

var CobolHighlightRules = function() {
    var keywords = "ACCEPT|ACCESS|ADD|ADDRESS|ADVANCING|AFTER|ALL|ALPHABET|ALPHABETIC|ALPHABETIC-LOWER|ALPHABETI--UPPER|ALPHANUMERIC|ALPHANUMERIC-EDITED|ALSO|ALTER|ALTERNATE|AND|ANY|APPLY|APPROXIMATE|ARE|AREA|AREAS|ASCENDING|ASSIGN|AT|AUTHOR|AUTOMATIC||BEFORE|BEG|NING|BINARY|BLANK|BLOCK|BOTTOM|BY||CALL|CANCEL|CD|CF|CH|CHARACTER|CHARACTERS|CLASS|CLOCK-UNITS|CLOSE|COBOL|CODE|CODE-SET|COLLATING|COLUMN|COMMA|COMMON|COMMUNICATION|COMP|COMP-1|COMP-2|COMP-3|COMP-4|COMP-5|COMP-X|COMPUTATIONAL|COMPUTATIONAL-1|COMPUTATIONAL-2|COMPUTATIONAL-3|COMPUTATIONAL-4|COMPUTATIONAL-5|COMPUTE|CONFIGURATION|CONTAINS|CONTENT|CONTINUE|CONTROL|CONTROLS|CONVERTING||COPY|CORR|CORRESPONDING|COUNT|CURRENCY||DATA|DATE|DATE-COMPILED|DATE-WRITTEN|DAY|DAY-OF-WEEK|DBCS|DE|DEBUG-CONTENTS|DEBUG-ITEM|DEBUG-LINE|DEBUG-NAME|DEBUG-SUB-1|DEBUG-SUB-2|DEBUG-SUB-3|DEBUGGING|DECIMAL-POINT|DECLARATIVES|DELETE|DELIMITED|DELIMITER|DEPENDING|DESCENDING|DESTINATION|DETAIL|DISABLE|DISK|DISPLAY|DISPLAY-1|DIVIDE|DIVISION|DOWN-2|DUPLICATES|DYNAMIC||EBCDIC|EGCS|EGI|EJECT|ELSE|EMI|ENABLE|END|END-ADD|END-ACCEPT|END-CALL|END-COMPUTE|END-DELETE|END-DIVIDE|END-EVALUATE|END-IF|END-MULTIPLY|END-OF-PAGE|END-PERFORM|END-READ|END-RECEIVE|END-RETURN|END-REWRITE|END-SEARCH|END-START|END-STRING|END-SUBTRACT|END-TALLYING|END-UNSTRING|END-WRITE|END-XML|ENDING|ENTER|ENTRY|ENVIRONMENT|EOP|EQUAL|EQUALS|ERROR|ESI|EVALUATE|EVERY|EXAMINE|EXCEPTION||EXIT|EXTEND|EXTERNAL||FALSE|FD|FILE|FILE-CONTROL|FILLER|FINAL|FIRST|FOOTING|FOR|FORMAT|FROM|FUNCTION|FUNCTION-POINTER|||GENERATE|GOBACK|GENERIC|GIVING|GLOBAL|GO|GREATER|GROUP|GROUP-USAGE||HEADING|HIGH-VALUE|HIGH-VALUES||I-O|I-O-CONTROL|ID|IDENTIFICATION|IF|IMPLICIT|IN|INDEX|INDEXED|INDICATE|INITIAL|INITIALIZE|INITIATE|INPUT|INPUT-OUTPUT|INSPECT|INSTALLATION|INTO|INVALID|INVOKE|IS||JUST|JUSTIFIED|JUSTIFY||KANJI|KEY||LABEL|LAST|LEADING|LEFT|LENGTH|LESS|LIKE|LIMIT|LIMITS|LINAGE|LINAGE-COUNTER|LINE|LINES|LINE-COUNTER|LINKAGE|LOCAL-STORAGE|LOCALE|LOCK|LOCKFILE|LOW-VALUE|LOW-VALUES||MEMORY|MERGE|MESSAGE|METHOD|MODE|MODULES|MORE-LABELS|MOVE|MULTIPLE|MULTIPLY||NATIONAL|NATIVE|NEGATIVE|NEW|NEXT|NO|NOT|NULL|NULLS|NUMBER|NUMERIC|NUMERIC-EDITED||OBJECT|OBJECT-COMPUTER|OCCURS|OF|OFF|OMITTED|ON|OPEN|OPTIONAL|OR|ORDER|ORGANIZATION|OTHER|OUTPUT|OVERFLOW||PACKED-DECIMAL|PADDING|PAGE|PAGE-COUNTER|PARSE|PASSWORD|PERFORM|PF|PH|fragment|PIC|fragment|PICTURE|PICTURE-IS|PLUS|POINTER|POSITION|POSITIONING|POSITIVE|PREVIOUS|PRINTER|PRINTING|PROCEDURE|PROCEDURE-POINTER|PROCEDURES|PROCEED|PROCESSING|PROGRAM|PROGRAM-ID|PROGRAM-STATUS|PROMPT|PROTECTED|PURGE||QUEUE|QUOTE|QUOTES||RANDOM|RD|READ|RECEIVE|RECEIVE-CONTROL|RECORD|RECORDING|RECORDS|RECURSIVE|REDEFINES|REEL|REFERENCE|REFERENCES|RELATIVE|RELEASE|REMAINDER|REMARKS|REMOVAL|RENAMES|REPLACE|REPLACING|REPLY|REPORT|REPORTING|REPORTS|RERUN|RESERVE|RESET|RETURNING|RETURN'-')|RETURN-CODE|RETURNED|REVERSED|REWIND|REWRITE|RF|RH|RIGHT|ROUNDED|RUN||SAME|SD|SEARCH|SECTION|SECURITY|SEGMENT|SEGMENT-LIMIT|SELECT|SELF|SEND|SENTENCE|SEPARATE|SEQUENCE|SEQUENTIAL|SET|SHARED|SHIFT-IN|SHIFT-OUT|SIGN|SIZE|SORT|SORT-CONTROL|SORT-CORE-SIZE|SORT-FILE-SIZE|SORT-MERGE|SORT-MESSAGE|SORT-MODE-SIZE|SORT-RETURN|SOURCE|SOURCE-COMPUTER|SPACE|SPACES|SPECIAL-NAMES|STANDARD|STANDARD-1|STANDARD-2|START|STATUS|STOP|STRING|SUB-QUEUE-1|SUB-QUEUE-2|SUB-QUEUE-3|SUBTRACT|SUM|SUPER|SUPPRESS|SYMBOL|SYMBOLIC|SYNC|SYNCHRONIZED||TABLE|TALLY|TALLYING|TAPE|TERMINAL|TERMINATE|TEST|TEXT|TIMESTAMP|THAN|THEN|THROUGH|THRU|TIME|TIMES|TO|TOP|TRAILING|TRANSFORM|TRUE|TYPE|TYPEDEF||UNIT|UNLOCK|UNLOCKFILE|UNLOCKRECORD|UNSTRING|UNTIL|UP-2|UPON|USAGE|USE|USING||VALUE|VALUES|VARYING||WHEN|WHEN-COMPILED|WITH|WORDS|WORKING-STORAGE|WRITE|WRITE-ONLY|XML||ZERO|ZEROS|ZEROES";

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

    this.$rules = {
        "start" : [{
            token : "dot",
            regex : "[\\.]"
        } ,{
            token : "variable",
            regex : "(^\\**)(?=PIC.*)"
        },{
            token : "sql",
            regex : "EXEC.*END-EXEC"
        },{
            token : "sql",
            regex : "EXEC.*",
            next  : "sqlEnd"
        },{
            token : "comment",
            regex : "^\\*.*"
        },{
            token : "comment",
            regex : "\\*.{0,65}"
        },{
            //去掉前面0-6列字符
            token :"identifier",
            regex:"^.{6}"
        },{
            token : "string",           // " string
            regex : '".*?"'
        },{
            token : "string",           // ' string
            regex : "'.*?'"
        },{
            token :keywordMapper,       // 匹配关键词
            regex : "\\b([a-zA-Z0-9][a-zA-Z0-9-]*)\\b"     
        }, {
            token : "constant.numeric", // float
            regex : "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b"
        },{
            token : "keyword.operator",
            regex : "\\+|\\/|\\/\\/|%|<@>|@>|<@|&|\\^|~|<|>|<=|=>|==|!=|<>|="
        }, {
            token : "paren.lparen",
            regex : "[\\(]"
        }, {
            token : "paren.rparen",
            regex : "[\\)]"
        }, {
            token : "text",
            regex : "\\s+"
        }],
        "sqlEnd":[{
            //去掉前面0-6列字符
            token :"identifier",
            regex:"^.{6}"
        },{
        	token: "sql",
        	regex: ".*END-EXEC",
        	next:	"start"
        },{
            token : "comment",
            regex : "\\*.{0,65}"
        },{
            token : "sql", // comment spanning whole line
            regex : ".+.$"
        }]
    };
};

oop.inherits(CobolHighlightRules, TextHighlightRules);

exports.CobolHighlightRules = CobolHighlightRules;
});

define("ace/mode/cobol",["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/cobol_highlight_rules"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var CobolHighlightRules = require("./cobol_highlight_rules").CobolHighlightRules;

var Mode = function() {
    this.HighlightRules = CobolHighlightRules;
    this.$behaviour = this.$defaultBehaviour;
};
oop.inherits(Mode, TextMode);

(function() {

    this.lineCommentStart = "*";

    this.$id = "ace/mode/cobol";
}).call(Mode.prototype);

exports.Mode = Mode;

});
                (function() {
                    window.require(["ace/mode/cobol"], function(m) {
                        if (typeof module == "object" && typeof exports == "object" && module) {
                            module.exports = m;
                        }
                    });
                })();
            