USE geneious_test_v8;
SET @rownum = 0;
SET sql_mode='PIPES_AS_CONCAT';

SELECT
	substr(code,instr(code,'.')+1) AS NOTE_TYPE_CODE,
    substr(code,23,instr(code,'.')-23) AS NOTE_TYPE_NAME,
	'A' || (@rownum:=@rownum + 1) || '("' || substr(code,instr(code,'.')+1) || '", "' || substr(code,23,instr(code,'.')-23) || '"),' as JAVA_ENUMS
FROM search_field 
WHERE  code like 'DocumentNoteUtilities-%'
ORDER BY IF(instr(code, '(Seq)')>0, 0, IF(instr(code, '(Samples)')>0, 1, IF(instr(code, '(CRS)')>0, 2, IF(instr(code, '(BOLD)')>0, 3, 4))))
LIMIT 1000;