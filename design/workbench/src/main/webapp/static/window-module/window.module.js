
'use strict';

var myWindow = angular.module('windowModule', []);

/**
 *创建value对象，初始化各个窗口的默认值展示形式
 *
 *output graph window ： grid
 *output table : tab
 *neo4j : tab
 *
 */
myWindow.value('output_graph_window_style', 'grid');
myWindow.value('output_table_window_style', 'tab');
myWindow.value('neo4j_window_style', 'tab');
myWindow.value('neo4j_window_show', false);
myWindow.value('shiny_report_show', false);
