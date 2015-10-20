create table "DB"."DBA"."DataIdBranches"
(
  "uribra" INTEGER IDENTITY,
  "uri" VARCHAR UNIQUE NOT NULL,
  "branch" VARCHAR NOT NULL,
  "master" INTEGER NOT NULL,
  "parent" VARCHAR,
  "head" BIGINT NOT NULL,
  "editkey" VARCHAR NOT NULL,
  "contact" VARCHAR NOT NULL,
  "changed" DATETIME NOT NULL,
  PRIMARY KEY ("uribra")
);

create table "DB"."DBA"."DataIdDeltas"
(
  "id" BIGINT NOT NULL,
  "uribra" INTEGER NOT NULL,
  "delta" INTEGER NOT NULL,
  "sizeadd" INTEGER NOT NULL,
  "sizedel" INTEGER NOT NULL,
  "attime" DATETIME NOT NULL,
  "agent" VARCHAR,
  PRIMARY KEY ("id")
);

commit work;
delay(2);

CREATE FUNCTION DB.DBA.ConvertToBase
(
    in value BIGINT,
    in base INTEGER
)
{
	if(base NOT IN (2, 4, 8, 16, 32))
		SIGNAL('ArithmeticError', 'ConvertToBase only works for base = 2,4,8,16 or 32');
	--mod does not work for BIGINT -> using bit_and(number, base-1) only works for base = 2^x
    DECLARE characters CHAR(36);
    DECLARE res VARCHAR;
    characters := '0123456789abcdefghijklmnopqrstuvwxyz';
    res := '';
    IF (value < 0 OR base < 2 OR base > 36) 
		RETURN NULL;
    WHILE(value > 0)
	{
        res := SUBSTRING(characters, bit_and(value, base-1) + 1, 1) || res;
        value := value / base;
	}
    RETURN res;
};

CREATE FUNCTION DB.DBA.ConvertFromBase
(
    in value VARCHAR,
    in base TINYINT
)
{
    DECLARE characters VARCHAR;
	DECLARE res BIGINT;
	DECLARE ind, chrind INTEGER;

    characters := '0123456789abcdefghijklmnopqrstuvwxyz';
    res := 0;
    ind := 0;
	value :=LOWER(TRIM(value));
	
    IF(base < 2 OR base > 36)
		RETURN NULL;
		
	 WHILE(ind < LENGTH(value))
	 {
            chrind := STRCHR(characters, SUBSTRING(value, LENGTH(value) - ind, 1));
			res := res + POWER(base, ind) * chrind ;
			ind := ind + 1;
	 }
    RETURN res;
};

CREATE FUNCTION DB.DBA.DataIdAddHiddenBitToId
(
    in value BIGINT,
	in addVal INTEGER DEFAULT 0
)
{
	value := bit_shift(value, 1);
	value := bit_or(value, addVal);
	return value;
};

create procedure DB.DBA.DataIdUpdateBranch(
in uribraa INTEGER, 
in kkey VARCHAR, 
in hhead BIGINT DEFAULT -1, 
in contactInfo VARCHAR DEFAULT NULL, 
in isMaster INTEGER DEFAULT -1)
{
	declare rnd VARCHAR;
	if(UPPER(kkey) = (SELECT editkey FROM DB.DBA.DataIdBranches WHERE uribra = uribraa))
	{
	if(hhead >= 0)
	UPDATE DB.DBA.DataIdBranches SET head = hhead, changed = now() WHERE uribra = uribraa;
	if(isMaster >= 0 AND isMaster < 2)
	UPDATE DB.DBA.DataIdBranches SET master = isMaster, changed = now() WHERE uribra = uribraa;
	if(contactInfo IS NOT NULL)
	UPDATE DB.DBA.DataIdBranches SET contact = contactInfo, changed = now() WHERE uribra = uribraa;
	return 'updated';
	}
	else
	SIGNAL ('ErrorOnUpdate', 'DataIdUpdateBranch: The provided edit-key is not correct.');
};
 
create procedure DB.DBA.DataIdAddDelta(
in uribraa INTEGER,
in kkey VARCHAR,
in addSize INTEGER, 
in delSize INTEGER,
in isNewHead INTEGER DEFAULT 0,
in aagent VARCHAR DEFAULT NULL)
{
	if(UPPER(kkey) = (SELECT editkey FROM DB.DBA.DataIdBranches WHERE "uribra" = uribraa))
	{
		DECLARE urii, branchName, hexx VARCHAR;
		DECLARE version, id, addId, delId  BIGINT;
		version := (SELECT (COALESCE(MAX(delta), -2) + 2) FROM DB.DBA.DataIdDeltas WHERE "uribra" = uribraa);
		id := DB.DBA.DataIdGenerateUniqueId(uribraa, version);
		INSERT INTO DB.DBA.DataIdDeltas VALUES(id , uribraa, version, addSize, delSize, now(), aagent);
		if(isNewHead)
			DB.DBA.DataIdUpdateBranch(uribraa, kkey, id, isNewHead);
		urii := (SELECT uri FROM DB.DBA.DataIdBranches WHERE "uribra" = uribraa);
		branchName := (SELECT branch FROM DB.DBA.DataIdBranches WHERE "uribra" = uribraa);
		addId := DB.DBA.DataIdAddHiddenBitToId(id, 0);
		delId := DB.DBA.DataIdAddHiddenBitToId(id, 1);
		DB.DBA.RDF_GRAPH_GROUP_INS (urii || '#' || branchName, urii || '#' || DB.DBA.ConvertToBase(addId, 16));
		DB.DBA.RDF_GRAPH_GROUP_INS (urii || '#' || branchName, urii || '#' || DB.DBA.ConvertToBase(delId, 16));
		return DB.DBA.ConvertToBase(id, 16);
	}
	SIGNAL ('ErrorOnUpdate', 'DataIdBranches: The provided edit-key is not correct.');
};

create procedure DB.DBA.DataIdADeleteDeltas(
in uribraa INTEGER,
in deltaa INTEGER,
in kkey VARCHAR)
{
	DECLARE state, msg, descs, rows, rowsy, query, i, urii, branchName any;
	if(UPPER(kkey) = (SELECT editkey FROM DB.DBA.DataIdBranches WHERE "uribra" = uribraa))
	{
		exec(sprintf('SELECT id FROM "DB"."DBA"."DataIdDeltas" WHERE uribra = %d AND delta >= %d', uribraa, deltaa), state, msg, vector (), 0, descs, rows);
		if(state > 0)
			SIGNAL(state, msg);
			
		urii := (SELECT uri FROM DB.DBA.DataIdBranches WHERE "uribra" = uribraa);
		branchName := (SELECT branch FROM DB.DBA.DataIdBranches WHERE "uribra" = uribraa);
		for(i := 0; i < length(rows); i:=i+1)
		{
			DB.DBA.RDF_GRAPH_GROUP_DEL(urii || '#' || branchName, urii || rows[i][0]);
			query := sprintf('SPARQL WITH <%s> DELETE {?s ?p ?o}', urii || rows[i][0]);
			exec(query, state, msg, vector (), 0, descs, rowsy);
		}
		return 1;
	}
	else
		SIGNAL ('ErrorOnUpdate', 'DataIdUpdateBranch: The provided edit-key is not correct.');
};

create procedure DB.DBA.DataIdDoesVersionExist(in versionUri VARCHAR)
{
	declare uriArray, uribraa, version any;
	uriArray := WS.WS.PARSE_URI(versionUri);
	if(uriArray[5] IS NULL OR length(uriArray[5]) < 10)
	SIGNAL('ErrorOnUricheck', 'DataIdDoesVersionExist: parent version uri is not a valid version uri -> http://yourhost.org/gjsg/fgh.ttl#7e81d0000000201');

	uribraa := DB.DBA.ConvertFromBase(uriArray[5], 16);

	if((SELECT COUNT(*) FROM DB.DBA.DataIdDeltas WHERE id = uribraa) = 0)
		return 'False';
	else
		return 'True';
};

create procedure DB.DBA.DataIdGetBranchInfo(
in urii VARCHAR, 
in branchName VARCHAR)
{
	DECLARE state, msg, descs, rows, query, i any;
	query := 'SELECT uribra, master, head, contact, changed FROM DB.DBA.DataIdBranches WHERE uri = \'%s\' AND branch = \'%s\'';
	exec(sprintf(query, urii, branchName ), state, msg, vector (), 0, descs, rows);
	exec_result_names (descs[0]);
	for(i:=0; i < length (rows); i:=i+1)
	{
		exec_result (rows[i]);
	}
};

create procedure DB.DBA.DataIdGetVersion(
in urii VARCHAR, 
in branchName VARCHAR, 
in delta INTEGER )
{
	if(mod(delta, 2) = 1)
	signal('ErrorOnRequest','DataIdGetVersion: delta (version) number has to be a non odd number');
	DECLARE state, msg, descs, rows, query, i any;
	query := sprintf('SELECT DB.DBA.RDF_FORMAT_RESULT_SET_AS_JSON ( vector ( "u"."g", "u"."s", "u"."p", "u"."o"), vector ( \'g\', \'s\' , \'p\' , \'o\' )) AS "callret-0" LONG VARCHAR FROM ( SELECT __id2in ( "t"."G") AS "g", __id2in ( "t"."S") AS "s", __id2in ( "t"."P") AS "p", __ro2sq ( "t"."O") AS "o"  FROM DB.DBA.RDF_QUAD AS "t" WHERE ( "t"."G" in ( DB.DBA.RDF_GRAPH_GROUP_LIST_GET ( __bft ( __bft( \'%s\' || \'#\' || \'%s\' , 1), 1), NULL, 0, NULL, NULL, 1))) OPTION (QUIETCAST) ) AS "u" OPTION (QUIETCAST)', urii, branchName);
	exec(query, state, msg, vector (), 0, descs, rows);
	return rows[0][0];
} ;

create procedure DB.DBA.DataIdGetLatestVersion(in uribraa INTEGER )
{
	declare idd, bra, urii any;
	idd := (SELECT id FROM DB.DBA.DataIdDeltas WHERE uribra = uribraa AND delta = (SELECT MAX(delta) FROM DB.DBA.DataIdDeltas WHERE uribra = uribraa) );
	bra := (SELECT branch FROM DB.DBA.DataIdBranches WHERE uribra = uribraa);
	urii := (SELECT uri FROM DB.DBA.DataIdBranches WHERE uribra = uribraa);
	return DB.DBA.DataIdGetVersion(urii, bra, idd);
} ;

create procedure DB.DBA.DataIdGetHeadVersion(in uribraa INTEGER )
{
	declare idd, bra, urii any;
	idd := (SELECT head FROM DB.DBA.DataIdBranches WHERE uribra = uribraa);
	bra := (SELECT branch FROM DB.DBA.DataIdBranches WHERE uribra = uribraa);
	urii := (SELECT uri FROM DB.DBA.DataIdBranches WHERE uribra = uribraa);
	return DB.DBA.DataIdGetVersion(urii, bra, idd);
} ;

create procedure DB.DBA.DataIdIsValidUriAndBranch(
in urii VARCHAR, 
in branchName VARCHAR)
{
	declare uriArray, i any;
	uriArray := WS.WS.PARSE_URI(urii);
	if(uriArray[0] <> 'http' AND uriArray[0] <> 'https')
	SIGNAL('ErrorOnUricheck', 'DataIdIsValidUriAndBranch: Uri does not strat with http(s)');
	if(strchr(uriArray[1], '.') IS NULL OR strchr(uriArray[1], '.') < 2)
	SIGNAL('ErrorOnUricheck', 'DataIdIsValidUriAndBranch: hostname not followed by a dot, or no hostname at all');
	if(length(uriArray[2]) < 3)
	SIGNAL('ErrorOnUricheck', 'DataIdIsValidUriAndBranch: filepath of uri is too short - make sure to have a filepath included after the top level domain: http://example.org/some/file/path ');
	if(strchr(uriArray[2], '/') <> 0)
	SIGNAL('ErrorOnUricheck', 'DataIdIsValidUriAndBranch: filename of uri not starting with a slashmake sure to have a filepath included after the top level domain: http://example.org/some/file/path');
	if(uriArray[5] IS NOT NULL AND length(uriArray[5]) > 0 )
	SIGNAL('ErrorOnUricheck', 'DataIdIsValidUriAndBranch: DataID uris should not contain fragments (\'#\' followed some characters at the end of the uri)');
	--check branchname
	for(i:=0; i < length(branchName); i:=i+1)
	{
	  if(branchName[i] < 48 OR (branchName[i]-48 > 9 AND branchName[i]-48 < 17) OR ( branchName[i]-48 > 42 AND branchName[i]-48 < 49) OR branchName[i]-48 > 74)
		SIGNAL('ErrorOnUricheck', 'DataIdIsValidUriAndBranch: branch name included illegal characters - please use only alphnumeric characters without whitespaces');
	}
	if((SELECT COUNT(*) FROM DB.DBA.DataIdBranches WHERE uri = urii AND branch = branchName) <> 0)
	SIGNAL('ErrorOnUricheck', 'DataIdIsValidUriAndBranch: this Uri & branch name is already in use');

	return 'True';
};

create procedure DB.DBA.DataIdReplaceLatestVersion(
in urii VARCHAR, 
in uribraa VARCHAR)
{
	DECLARE state, msg, descs, rows, query any;
	DECLARE big BIGINT;
	big := DB.DBA.ConvertFromBase(uribraa, 16);
	big := bit_shift (big, -8); --delete laste 8 bits
	big := bit_shift (big, 9); -- add hidden bit
	msg := DB.DBA.ConvertToBase(big, 16);
	query := 'SPARQL  WITH <' || urii || '#' || msg || '>\n' ||
						'DELETE {?s ?p ?o} \n' ||
						'INSERT { <' || urii || '> <http://dataid.dbpedia.org/ns/core#latestDataIdVersion> <' || (urii || '#' || uribraa) || '>.} \n' ||
						'WHERE  { ?s <http://dataid.dbpedia.org/ns/core#latestDataIdVersion> ?o. }';

	exec(query, state, msg, vector (), 0, descs, rows);
	if(state > 0)
	SIGNAL(state, 'DataIdReplaceLatestVersion: An error occurred: ' || msg);
	return 'True';
};

create procedure DB.DBA.RandomKey(in lengthh INTEGER)
{
	randomize(rnd(10000));
	declare exarray, zw ANY;
	exarray := make_string (lengthh);
	declare i INTEGER;
	zw := 10;
	for(i:=0;i<lengthh ;i:=i+1){
	while((zw > 9 AND zw <17))
	{
	   zw := rnd(43);
	}
	aset(exarray, i, zw + 48);
	zw:=10;
	}
	return exarray;
};

create procedure DB.DBA.DataIdNewBranch(
in urii VARCHAR, 
in branchName VARCHAR, 
in contactInfo VARCHAR, 
in parentVersion VARCHAR,
in isMaster INTEGER DEFAULT 0)
{
	declare rnd VARCHAR;
	if((SELECT DB.DBA.DataIdIsValidUriAndBranch(urii , branchName)) = 'True')
	{
		if(parentVersion IS NULL OR parentVersion = '')
			isMaster := 1;
		if((SELECT COUNT(*) FROM DB.DBA.RDF_GRAPH_GROUP WHERE RGG_IRI = urii || '#' || branchName) = 0)
			DB.DBA.RDF_GRAPH_GROUP_CREATE (urii || '#' || branchName, 0);
		rnd := (SELECT DB.DBA.RandomKey(10));
		INSERT INTO DB.DBA.DataIdBranches("uri", "branch", "master", "parent", "head", "editkey", "contact", "changed") 
			VALUES(urii, branchName, isMaster, parentVersion, -1, rnd, contactInfo, now());
		return rnd || '#' || CAST(identity_value () AS VARCHAR);
	}
	return null;
};

create procedure UTC_TIME(in datum DATETIME := null)
{
	if(datum IS NULL)
		datum := now();
		
	return DATEADD('minute', timezone(datum)*(-1), datum);
};

create procedure DB.DBA.DataIdGenerateUniqueId(in uribra INTEGER, in version INTEGER)
{
	DECLARE state, msg, descs, rows, query, i any;
	DECLARE big BIGINT;
	rows := 0;
	big :=0;
	big := bit_shift (datediff ('minute',  stringdate ('2000.01.01'), UTC_TIME(now())), 36); --first 28 bits of ID = minutes since 1.1.2000 / 500 years to go
	big := bit_or(big, bit_shift (uribra, 8));	--28 bits for uri/branch id -> 268.435.456 branches 
	big := bit_or(big, bit_shift(version, -1)); --version numbers are always non odd -> we save one bit = 256 version a branch possible
	return big;
};

create procedure DB.DBA.DataIdResolveUriBranch(in uribraa INTEGER)
{
	DECLARE state, msg, descs, rows, query, i any;
        exec(sprintf('SELECT uri, branch FROM DB.DBA.DataIdBranches WHERE uribra = %d', uribraa), state, msg, vector (), 0, descs, rows);
	exec_result_names (descs[0]);
	for(i:=0; i < length (rows); i:=i+1)
	{
		exec_result (rows[i]);
	}
};

create procedure DB.DBA.DataIdResolveUri(in urii VARCHAR, in braa VARCHAR)
{
	DECLARE state, msg, descs, rows, query, i any;
        exec(sprintf('SELECT uribra FROM DB.DBA.DataIdBranches WHERE uri = \'%s\' AND branch = \'%s\'', urii, braa), state, msg, vector (), 0, descs, rows);
	exec_result_names (descs[0]);
	for(i:=0; i < length (rows); i:=i+1)
	{
		exec_result (rows[i]);
	}
};