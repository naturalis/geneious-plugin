SET sql_mode='PIPES_AS_CONCAT'; 

SET @rownum = 0;

SELECT  'A' || (@rownum:=@rownum + 1) || '("' || substr(code,instr(code,'.')+1) || '", "' || substr(code,23,instr(code,'.')-23) || '"),' as code 
INTO OUTFILE '/home/ayco/tmp/geneious-note-types.txt'
FROM search_field 
WHERE  code like 'DocumentNoteUtilities-%'
ORDER BY IF(instr(code, '(Seq)')>0, 0, IF(instr(code, '(Samples)')>0, 1, IF(instr(code, '(CRS)')>0, 2, 3)))
LIMIT 1000;
