match (n:Paragraph) 
where not blm.content(n.location) contains 'EXIT' or n.name contains 'RETURN' 
return n.name as name, blm.content(n.location) as content, 
n.startLine as startLine, n.endLine as endLine;