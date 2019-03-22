<html>
<head>
<style type="text/css">
		#p1
		{
			width: 90%;
		}
		#p2
		{
			width: 90%;
		}
		#p3
		{
			width: 40%;
		}
		#p4
		{
			width: 80%;
		}
		#p5
		{
			width: 60%;
		}
		#p6
		{
			width: 60%;
		}
		#p7
		{
			width: 80%;
		}
		.myh1
		{
			margin: 5% auto 3% auto;
		}
		.myh2
		{
			margin: 0 auto 0 5%;
		}
		.myh3
		{
			text-align: left;
			margin: 5% auto 1% 5%;
			width: 40%;
			color: #9AC0CD;
		}
		#myul
		{
			margin: 1% auto 0 5%;
		}
		.myli
		{
			margin: 1% auto 0 0;
		}
        .myTable
        {
            border-collapse: collapse;
            margin: 0 auto 0 5%;
            text-align: center;
        }
        .myTable td, table th
        {
            border: 1px solid #cad9ea;
            color: #666;
            height: 30px;
        }
        .myTable thead th
        {
            background-color: #CCE8EB;
            width: 100px;
        }
        .myTable tr:nth-child(odd)
        {
            background: #fff;
        }
        .myTable tr:nth-child(even)
        {
            background: #F5FAFA;
        }
        .myTable1
        {
            border-collapse: collapse;
            margin: 0 auto 0 5%;
            text-align: left;
        }
        .myTable1 td, table th
        {
            border: 1px solid #cad9ea;
            color: #666;
            height: 30px;
        }
        .myTable1 thead th
        {
            background-color: #CCE8EB;
            width: 100px;
        }
        .myTable1 tr:nth-child(odd)
        {
            background: #fff;
        }
        .myTable1 tr:nth-child(even)
        {
            background: #F5FAFA;
        }
		#table6
		{
			margin-bottom: 5%;
		}
		.several
		{
			margin: 5% auto 1% 5%;
		}
		.myText
		{
			margin: 2% auto 2% 8%;
			color: #9AC0CD;
			float: none;
		}
    </style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>System Documentation</title>
</head>
<body>
<h1 class="myh1">System Documentation</h1>

<h2 class="myh2">Project Name : </h2>
<h3 class="myText">${projectName}</h3>
<h2 class="myh2">Project Description : </h2>
<h3 class="myText">${projectDescription}</h3>
<h2 class="myh2">Contents</h2>
<ul id="myul">
  <li class="myli"><a href="#t1">Summary</a></li>
  <li class="myli"><a href="#t2">Programs</a></li>
  <li class="myli"><a href="#t3">Paragraphs</a></li>
  <li class="myli"><a href="#t4">Table overview</a></li>
  <li class="myli"><a href="#t5">Copybooks</a></li>
  <li class="myli"><a href="#t6">Sql_Logic</a></li>
</ul>

<a name="t1"></a>
<h3 id="p1" class="myh3">Summary</h3>
<table width="90%" class="myTable">
	<tr>
		<#list summaryDetails as summaryDetail>
			<th>${summaryDetail.detailName}</th>	
		</#list>
	</tr>
	<tr>
		<#list summaryDetails as summaryDetail>
			<td>${summaryDetail.detailData}</td>	
		</#list>
	</tr>
</table>
<a name="t2"></a>
<h3 id="p2" class="myh3">Programs</h3>
<table width="90%" class="myTable">
	<tr>
		<th>Name</th>
		<th>Type</th>
		<th>Lines</th>
		<th>Complexity</th>
		<th>Tags</th>
	</tr>
	<#list programDetails as programDetail>
		<tr>
			<td>${programDetail.name}</td>	
			<td>${programDetail.type}</td>	
			<td>${programDetail.lines}</td>	
			<td>${programDetail.complexity}</td>	
			<td>${programDetail.tags}</td>	
		</tr>
	</#list>
</table>
<a name="t3"></a>
<h3 id="p3" class="myh3">Paragraphs</h3>
<table width="90%" class="myTable">
	<tr>
		<th>Name</th>
		<th>Lines</th>
		<th>Compelexity</th>
		<th>Tags</th>
	</tr>
	<#list paraDetails as paraDetail>
		<tr>
			<td>${paraDetail.paragraphName}</td>
			<td>${paraDetail.lines}</td>
			<td>${paraDetail.complexity}</td>
			<td>${paraDetail.tags}</td>
		</tr>
	</#list>
</table>
<a name="t4"></a>
<h3 id="p4" class="myh3">Table overview</h3>
<table width="45%" class="myTable">
	<tr>
		<th>Table Name</th>
		<th>Tags</th>
	</tr>
	<#list usedInItems as usedInItem>
		<tr>
			<td><a href="#${usedInItem.nodeId}">${usedInItem.name}</a></td>
			<td>${usedInItem.tags}</td>
		</tr>
	</#list>
</table>
<#list usedInItems as usedInItem>
	<h3 id="${usedInItem.nodeId}" class="myh3">Table detail(definition) : ${usedInItem.name}</h3>
	<table width="30%" class="myTable1">
		<tr>
			<th>Column/Field Name</th>
			<th>Column/Field Type</th>
		</tr>
		<#list tableColumnDetails as tableColumnDetail>
			<#if tableColumnDetail.tableNodeId == usedInItem.nodeId>
				<#list tableColumnDetail.allTableDetailItems as allTableDetailItem>
					<tr>
						<td>
							${allTableDetailItem.name}
						</td>
						<td>
							${allTableDetailItem.type}
						</td>
					</tr>
				</#list>
			</#if>
		</#list>	
	</table>
	<h3 class="myh3">Table detail(usage) : ${usedInItem.name}</h3>
	<table width="30%" class="myTable1">
		<tr>
			<th>Used In Paragraph</th>
		</tr>
		<tr>
			<td>
				<#list usedInItem.usedIn as paragraphUseTableInfo>
					${paragraphUseTableInfo.paragraphName}</br>
				</#list>
			</td>
		</tr>
	</table>
</#list>
<a name="t5"></a>
<h3 id="p5" class="myh3">Copybooks</h3>
<table width="60%" class="myTable">
	<tr>
		<th>Name</th>
		<th>Type</th>
		<th>Tags</th>
		<th>Used In Program</th>
	</tr>
	<#list copyBookDetails as copyBookDetail>
		<tr>
			<td>${copyBookDetail.name}</td>
			<td>${copyBookDetail.type}</td>
			<td>${copyBookDetail.tags}</td>
			<td>
		 		<#list copyBookDetail.usedInPrograms as usedInProgram>
					${usedInProgram.name}</br>
				</#list>
			</td>
		</tr>
	</#list>
</table>
<a name="t6"></a>
<h3 id="p6" class="myh3">Sql Logics</h3>
<table width="80%" class="myTable" id="table6">
	<tr>
		<th>Program</th>
		<th>Paragraph</th>
		<th>Command</th>
		<th>Table</th>
	</tr>
	<#list sqlLogicItems as sqlLogicItem>
		<tr id="${sqlLogicItem.paragraphName}${sqlLogicItem.tableName}${sqlLogicItem.operation}">
			<td>${sqlLogicItem.programName}</td>
			<td>${sqlLogicItem.paragraphName}</td>
			<td>${sqlLogicItem.operation}</td>
			<td>${sqlLogicItem.tableName}</td>
		</tr>
	</#list>
</table>
</body>
</html>