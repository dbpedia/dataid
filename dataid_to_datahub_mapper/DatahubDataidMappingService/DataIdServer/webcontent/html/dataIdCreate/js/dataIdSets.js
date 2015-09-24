/**
 * Created by Chile on 9/12/2015.
 */

function getContext()
{
    var c = {
        "dataid": "http://dataid.dbpedia.org/ns/core#",
        "dc": "http://purl.org/dc/terms/",
        "dcat": "http://www.w3.org/ns/dcat#",
        "foaf": "http://xmlns.com/foaf/0.1/",
        "owl": "http://www.w3.org/2002/07/owl#",
        "prov": "http://www.w3.org/ns/prov#",
        "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
        "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
        "sd": "http://www.w3.org/ns/sparql-service-description#",
        "void": "http://rdfs.org/ns/void#",
        "rut" : "http://rdfunit.aksw.org/ns/core#",
        "xsd": "http://www.w3.org/2001/XMLSchema#"
    };
/*    var dataid = getLdToAngularMap(getEmptyDataId('dzdzttt'));
    angular.extend(c, dataid);
    var dataset = getLdToAngularMap(getEmptyDataset('zzzzg', dataid['@id']));
    angular.extend(c, dataset);
    var dist = getLdToAngularMap(getEmptyDistribution('ftdffhghj', dataset['@id'], ['dataid:Distribution']));
    angular.extend(c, dist);
    var agent = getLdToAngularMap(getEmptyAgent('ddfgrrrs', ['dataid:Creator']));
    angular.extend(c, agent);*/
    return c;
}


function getLicenses()
{
    return [
        {name: 'Creative Commons Attribution 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by'},
        {name: 'Creative Commons Attribution-ShareAlike 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by-sa'},
        {name: 'Creative Commons Attribution-NoDerivatives 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by-nd'},
        {name: 'Creative Commons Attribution-NonCommercial 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by-nc'},
        {name: 'Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International', val: 'http://purl.oclc.org/NET/rdflicense/cc-by-nc-nd'},
        {name: 'Creative Commons Public Domain Dedication', val: 'http://purl.oclc.org/NET/rdflicense/cc-zero'},
        {name: 'Open Data Commons Attribution License', val: 'http://purl.oclc.org/NET/rdflicense/odc-by'},
        {name: 'Open Data Commons Public Domain Dedication and License', val: 'http://purl.oclc.org/NET/rdflicense/odc-pddl'},
        {name: 'GNU General Public License', val: 'http://purl.oclc.org/NET/rdflicense/gpl-3.0'}
    ];
}

function getLanguages()
{
    return [
        {"code":"zu", "lang":"Zulu"},
        {"code":"za", "lang":"Zhuang"},
        {"code":"yo", "lang":"Yoruba"},
        {"code":"yi", "lang":"Yiddish"},
        {"code":"xh", "lang":"Xhosa"},
        {"code":"wo", "lang":"Wolof"},
        {"code":"vls", "lang":"West"},
        {"code":"fy", "lang":"West"},
        {"code":"cy", "lang":"Welsh"},
        {"code":"war", "lang":"Waray-Waray"},
        {"code":"wa", "lang":"Walloon"},
        {"code":"vo", "lang":"Volapük"},
        {"code":"vi", "lang":"Vietnamese"},
        {"code":"vec", "lang":"Venetian"},
        {"code":"ve", "lang":"Venda"},
        {"code":"fiu-vro", "lang":"Võro"},
        {"code":"uz", "lang":"Uzbek"},
        {"code":"ug", "lang":"Uyghur"},
        {"code":"ur", "lang":"Urdu"},
        {"code":"uk", "lang":"Ukrainian"},
        {"code":"udm", "lang":"Udmurt"},
        {"code":"tw", "lang":"Twi"},
        {"code":"tk", "lang":"Turkmen"},
        {"code":"tr", "lang":"Turkish"},
        {"code":"tum", "lang":"Tumbuka"},
        {"code":"tn", "lang":"Tswana"},
        {"code":"ts", "lang":"Tsonga"},
        {"code":"to", "lang":"Tonga"},
        {"code":"tpi", "lang":"Tok"},
        {"code":"ti", "lang":"Tigrinya"},
        {"code":"bo", "lang":"Tibetan"},
        {"code":"th", "lang":"Thai"},
        {"code":"tet", "lang":"Tetum"},
        {"code":"te", "lang":"Telugu"},
        {"code":"tt", "lang":"Tatar"},
        {"code":"ta", "lang":"Tamil"},
        {"code":"tg", "lang":"Tajik"},
        {"code":"ty", "lang":"Tahitian"},
        {"code":"tl", "lang":"Tagalog"},
        {"code":"sv", "lang":"Swedish"},
        {"code":"ss", "lang":"Swati"},
        {"code":"sw", "lang":"Swahili"},
        {"code":"su", "lang":"Sundanese"},
        {"code":"es", "lang":"Spanish"},
        {"code":"nr", "lang":"South"},
        {"code":"st", "lang":"Southern"},
        {"code":"so", "lang":"Somalia"},
        {"code":"sl", "lang":"Slovenian"},
        {"code":"sk", "lang":"Slovak"},
        {"code":"si", "lang":"Sinhalese"},
        {"code":"sd", "lang":"Sindhi"},
        {"code":"simple", "lang":"Simple"},
        {"code":"scn", "lang":"Sicilian"},
        {"code":"ii", "lang":"Sichuan"},
        {"code":"sn", "lang":"Shona"},
        {"code":"sh", "lang":"Serbo-Croatian"},
        {"code":"sr", "lang":"Serbian"},
        {"code":"gd", "lang":"Scottish"},
        {"code":"sco", "lang":"Scots"},
        {"code":"sc", "lang":"Sardinian"},
        {"code":"sa", "lang":"Sanskrit"},
        {"code":"sg", "lang":"Sango"},
        {"code":"bat-smg", "lang":"Samogitian"},
        {"code":"sm", "lang":"Samoan"},
        {"code":"rw", "lang":"Rwandi"},
        {"code":"ru", "lang":"Russian"},
        {"code":"rmy", "lang":"Romani"},
        {"code":"ro", "lang":"Romanian"},
        {"code":"ksh", "lang":"Ripuarian"},
        {"code":"rm", "lang":"Raeto"},
        {"code":"qu", "lang":"Quechua"},
        {"code":"pt", "lang":"Portuguese"},
        {"code":"pl", "lang":"Polish"},
        {"code":"pms", "lang":"Piedmontese"},
        {"code":"ff", "lang":"Peul"},
        {"code":"pdc", "lang":"Pennsylvania"},
        {"code":"ps", "lang":"Pashto"},
        {"code":"pap", "lang":"Papiamentu"},
        {"code":"pa", "lang":"Panjabi"},
        {"code":"pag", "lang":"Pangasinan"},
        {"code":"pi", "lang":"Pali"},
        {"code":"os", "lang":"Ossetian"},
        {"code":"om", "lang":"Oromo"},
        {"code":"or", "lang":"Oriya"},
        {"code":"oj", "lang":"Ojibwa"},
        {"code":"oc", "lang":"Occitan"},
        {"code":"no", "lang":"Norwegian"},
        {"code":"nn", "lang":"Norwegian"},
        {"code":"nd", "lang":"North"},
        {"code":"se", "lang":"Northern"},
        {"code":"nso", "lang":"Northern"},
        {"code":"nrm", "lang":"Norman"},
        {"code":"pih", "lang":"Norfolk"},
        {"code":"new", "lang":"Newar"},
        {"code":"ne", "lang":"Nepali"},
        {"code":"nap", "lang":"Neapolitan"},
        {"code":"ng", "lang":"Ndonga"},
        {"code":"nv", "lang":"Navajo"},
        {"code":"na", "lang":"Nauruan"},
        {"code":"nah", "lang":"Nahuatl"},
        {"code":"mn", "lang":"Mongolian"},
        {"code":"mo", "lang":"Moldovan"},
        {"code":"cdo", "lang":"Min"},
        {"code":"zh-min-nan", "lang":"Minnan", "lang":""},
        {"code":"min", "lang":"Minangkabau"},
        {"code":"mh", "lang":"Marshallese"},
        {"code":"mr", "lang":"Marathi"},
        {"code":"mi", "lang":"Maori"},
        {"code":"gv", "lang":"Manx"},
        {"code":"man", "lang":"Mandarin"},
        {"code":"mt", "lang":"Maltese"},
        {"code":"ms", "lang":"Malay"},
        {"code":"ml", "lang":"Malayalam"},
        {"code":"mg", "lang":"Malagasy"},
        {"code":"mk", "lang":"Macedonian"},
        {"code":"lb", "lang":"Luxembourgish"},
        {"code":"nds", "lang":"Low"},
        {"code":"dsb", "lang":"Lower"},
        {"code":"lmo", "lang":"Lombard"},
        {"code":"jbo", "lang":"Lojban"},
        {"code":"lt", "lang":"Lithuanian"},
        {"code":"ln", "lang":"Lingala"},
        {"code":"li", "lang":"Limburgian"},
        {"code":"lij", "lang":"Ligurian"},
        {"code":"lv", "lang":"Latvian"},
        {"code":"la", "lang":"Latin"},
        {"code":"lo", "lang":"Laotian"},
        {"code":"lan", "lang":"Lango"},
        {"code":"lad", "lang":"Ladino"},
        {"code":"ku", "lang":"Kurdish"},
        {"code":"kj", "lang":"Kuanyama"},
        {"code":"ko", "lang":"Korean"},
        {"code":"kg", "lang":"Kongo"},
        {"code":"kv", "lang":"Komi"},
        {"code":"tlh", "lang":"Klingon"},
        {"code":"rn", "lang":"Kirundi"},
        {"code":"ky", "lang":"Kirghiz"},
        {"code":"ki", "lang":"Kikuyu"},
        {"code":"khw", "lang":"Khowar"},
        {"code":"kk", "lang":"Kazakh"},
        {"code":"csb", "lang":"Kashubian"},
        {"code":"ks", "lang":"Kashmiri"},
        {"code":"pam", "lang":"Kapampangan"},
        {"code":"kr", "lang":"Kanuri"},
        {"code":"kn", "lang":"Kannada"},
        {"code":"xal", "lang":"Kalmyk"},
        {"code":"jv", "lang":"Javanese"},
        {"code":"ja", "lang":"Japanese"},
        {"code":"it", "lang":"Italian"},
        {"code":"ga", "lang":"Irish"},
        {"code":"ik", "lang":"Inupiak"},
        {"code":"iu", "lang":"Inuktitut"},
        {"code":"ie", "lang":"Interlingue"},
        {"code":"ia", "lang":"Interlingua"},
        {"code":"id", "lang":"Indonesian"},
        {"code":"ilo", "lang":"Ilokano"},
        {"code":"ig", "lang":"Igbo"},
        {"code":"io", "lang":"Ido"},
        {"code":"is", "lang":"Icelandic"},
        {"code":"hu", "lang":"Hungarian"},
        {"code":"ho", "lang":"Hiri"},
        {"code":"hi", "lang":"Hindi"},
        {"code":"hz", "lang":"Herero"},
        {"code":"he", "lang":"Hebrew"},
        {"code":"haw", "lang":"Hawaiian"},
        {"code":"ha", "lang":"Hausa"},
        {"code":"hak", "lang":"Hakka"},
        {"code":"ht", "lang":"Haitian"},
        {"code":"gu", "lang":"Gujarati"},
        {"code":"gn", "lang":"Guarani"},
        {"code":"kl", "lang":"Greenlandic"},
        {"code":"el", "lang":"Greek"},
        {"code":"got", "lang":"Gothic"},
        {"code":"gil", "lang":"Gilbertese"},
        {"code":"de", "lang":"German"},
        {"code":"ka", "lang":"Georgian"},
        {"code":"gan", "lang":"Gan"},
        {"code":"lg", "lang":"Ganda"},
        {"code":"gl", "lang":"Galician"},
        {"code":"fur", "lang":"Friulian"},
        {"code":"fr", "lang":"French"},
        {"code":"fi", "lang":"Finnish"},
        {"code":"fj", "lang":"Fijian"},
        {"code":"far", "lang":"Farsi"},
        {"code":"fo", "lang":"Faroese"},
        {"code":"ext", "lang":"Extremaduran"},
        {"code":"ee", "lang":"Ewe"},
        {"code":"et", "lang":"Estonian"},
        {"code":"eo", "lang":"Esperanto"},
        {"code":"en", "lang":"English"},
        {"code":"dz", "lang":"Dzongkha"},
        {"code":"nl", "lang":"Dutch"},
        {"code":"nds-nl", "lang":"Dutch Low Saxon"},
        {"code":"dv", "lang":"Divehi"},
        {"code":"diq", "lang":"Dimli"},
        {"code":"da", "lang":"Danish"},
        {"code":"cs", "lang":"Czech"},
        {"code":"hr", "lang":"Croatian"},
        {"code":"cr", "lang":"Cree"},
        {"code":"mus", "lang":"Creek"},
        {"code":"co", "lang":"Corsican"},
        {"code":"kw", "lang":"Cornish"},
        {"code":"zh-classical", "lang":"Classical Chinese"},
        {"code":"cv", "lang":"Chuvash"},
        {"code":"cho", "lang":"Choctaw"},
        {"code":"zh", "lang":"Chinese"},
        {"code":"ny", "lang":"Chichewa"},
        {"code":"chy", "lang":"Cheyenne"},
        {"code":"chr", "lang":"Cherokee"},
        {"code":"ce", "lang":"Chechen"},
        {"code":"ch", "lang":"Chamorro"},
        {"code":"ceb", "lang":"Cebuano"},
        {"code":"ca", "lang":"Catalan"},
        {"code":"zh-yue", "lang":"Cantonese", "lang":""},
        {"code":"km", "lang":"Cambodian"},
        {"code":"my", "lang":"Burmese"},
        {"code":"bxr", "lang":"Buriat"},
        {"code":"bg", "lang":"Bulgarian"},
        {"code":"bug", "lang":"Buginese"},
        {"code":"br", "lang":"Breton"},
        {"code":"bs", "lang":"Bosnian"},
        {"code":"bi", "lang":"Bislama"},
        {"code":"bpy", "lang":"Bishnupriya"},
        {"code":"bcl", "lang":"Bikol"},
        {"code":"bh", "lang":"Bihari"},
        {"code":"bn", "lang":"Bengali"},
        {"code":"be-x-old", "lang":"Belarusian"},
        {"code":"be", "lang":"Belarusian"},
        {"code":"bar", "lang":"Bavarian"},
        {"code":"eu", "lang":"Basque"},
        {"code":"ba", "lang":"Bashkir"},
        {"code":"map-bms", "lang":"Banyumasan"},
        {"code":"bm", "lang":"Bambara"},
        {"code":"az", "lang":"Azerbaijani"},
        {"code":"ay", "lang":"Aymara"},
        {"code":"av", "lang":"Avar"},
        {"code":"ast", "lang":"Asturian"},
        {"code":"as", "lang":"Assamese"},
        {"code":"frp", "lang":"Arpitan"},
        {"code":"roa-rup", "lang":"Aromanian"},
        {"code":"hy", "lang":"Armenian"},
        {"code":"arc", "lang":"Aramaic"},
        {"code":"an", "lang":"Aragonese"},
        {"code":"ar", "lang":"Arabic"},
        {"code":"ang", "lang":"Anglo-Saxon"},
        {"code":"am", "lang":"Amharic"},
        {"code":"als", "lang":"Alemannic"},
        {"code":"sq", "lang":"Albanian"},
        {"code":"ak", "lang":"Akan"},
        {"code":"af", "lang":"Afrikaans"},
        {"code":"aa", "lang":"Afar"},
        {"code":"ab", "lang":"Abkhazian"}
    ];
}

function getEmptyDataId(id) {
    var dataid = {
        "@graph": [{
            "@id": id,
            "@type": ["void:DatasetDescription"],
            "dataid:associatedAgent": {
                "@required": true,
                "@type":["dataid:Agent"],
                "@value": []
            },
            "foaf:topic": {
                "@required": true,
                "@type":["dataid:Dataset"],
                "@value": []
            },
            "dc:description": {
                "@required": false,
                "@language": null,
                "@value": {}
            },
            "dc:issued": {
                "@required": false,
                "@type": ["xsd:date"],
                "@value": null
            },
            "dc:modified": {
                "@required": false,
                "@type": ["xsd:date"],
                "@value": null
            },
            "rdfs:label": {
                "@required": false,
                "@language": null,
                "@value": {}
            },
            "dc:title": {
                "@required": true,
                "@type": ["xsd:string"],
                "@value": id.substring(Math.max(id.lastIndexOf('#')+1, id.lastIndexOf('/')+1))
            }
        }],
        "@context": {
            "dataid": "http://dataid.dbpedia.org/ns/core#",
            "dc": "http://purl.org/dc/terms/",
            "dcat": "http://www.w3.org/ns/dcat#",
            "foaf": "http://xmlns.com/foaf/0.1/",
            "owl": "http://www.w3.org/2002/07/owl#",
            "prov": "http://www.w3.org/ns/prov#",
            "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
            "sd": "http://www.w3.org/ns/sparql-service-description#",
            "void": "http://rdfs.org/ns/void#",
            "xsd": "http://www.w3.org/2001/XMLSchema#"
        }
    };
    return dataid;
}

function getEmptyDataset(id, parentId) {
    var dataset = {
        "@id": id,
        "@type": ["dataid:Dataset"],
        "@parent": parentId,
        "@pristine": true,
        "dataid:associatedAgent": {
            "@required": true,
            "@type":["dataid:Agent"],
            "@value": []
        },
        "dataid:licenseName": {
            "@required": false,
            "@type":["xsd:string"],
            "@value":null
        },
        "dc:description": {
            "@required": false,
            "@language": null,
            "@value": {}
        },
        "dc:issued": {
            "@required": false,
            "@type": ["xsd:date"],
            "@value": null
        },
        "dc:language": {
            "@required": false,
            "@type": ["xsd:string"],
            "@value": null
        },
        "dc:license":{
            "@required": true,
            "@type":["dc:LicenseDocument"],
            "@id": null
        },
        "dc:modified": {
            "@required": false,
            "@type": ["xsd:date"],
            "@value": null
        },
        "void:entities":{
            "@required": false,
            "@type": ["xsd:integer"],
            "@value": null
        },
        "void:classes":{
            "@required": false,
            "@type": ["xsd:integer"],
            "@value": null
        },
        "void:distinctObjects":{
            "@required": false,
            "@type": ["xsd:integer"],
            "@value": null
        },
        "dc:accrualPeriodicity":{
            "@required": false,
            "@type": ["dc:Frequency"],
            "@id": null
        },
        "dc:rights": {
            "@required": false,
            "@language": null,
            "@value": {}
        },
        "dc:title": {
            "@required": true,
            "@type": ["xsd:string"],
            "@value": id.substring(Math.max(id.lastIndexOf('#')+1, id.lastIndexOf('/')+1))
        },
        "void:exampleResource": {
            "@required": false,
            "@type":["rdfs:Resource"],
            "@value": []
        },
        "void:rootResource": {
            "@required": false,
            "@id": null
        },
        "void:sparqlEndpoint": {
            "@required": false,
            "@type":["dataid:SparqlEndpoint"],
            "@value": []
        },
        "void:triples": {
            "@required": false,
            "@type": ["xsd:integer"],
            "@value": null
        },
        "rdfs:label": {
            "@required": false,
            "@language": null,
            "@value": {}
        },
        "dcat:distribution": {
            "@required": true,
            "@type":["dataid:Distribution"],
            "@value": []
        },
        "dcat:keyword": {
            "@required": true,
            "@type":["xsd:string"],
            "@value": []
        },
        "void:subset": {
            "@required": false,
            "@type":["dataid:Dataset"],
            "@value": []
        },
        "dcat:landingPage": {
            "@required": true,
            "@id": null
        },
        "dataid:previousVersion": {
            "@required": false,
            "@id": null,
            "@type": ["dataid:Dataset"]
        },
        "dataid:nextVersion": {
            "@required": false,
            "@id": null,
            "@type": ["dataid:Dataset"]
        },
        "dataid:latestVersion": {
            "@required": false,
            "@id": null,
            "@type":["dataid:Dataset"]
        },
        "dataid:containsLinks":{
            "@required": false,
            "@id":null,
            "@type":["dataid:Linkset"]
        }
    };
    return dataset;
}

function getEmptyDistribution(id, parentId, types)
{
    var distr = {
        "@id": id,
        "@type": [].concat(types),
        "@parent": parentId,
        "@pristine": true,
        "dc:description": {
            "@required": false,
            "@language": null,
            "@value": {}
        },
        "dc:issued": {
            "@required": false,
            "@type": ["xsd:date"],
            "@value": null
        },
        "dc:modified": {
            "@required": false,
            "@type": ["xsd:date"],
            "@value": null
        },
        "dc:format": {
            "@required": true,
            "@type": ["xsd:string"],
            "@value": null
        },
        "dc:title":{
            "@required": true,
            "@type": ["xsd:string"],
            "@value": id.substring(Math.max(id.lastIndexOf('#')+1, id.lastIndexOf('/')+1))
        },
        "rdfs:label": {
            "@required": false,
            "@language": null,
            "@value": {}
        },
        "dcat:downloadURL": {
            "@required": true,
            "@id": null
        },
        "dcat:accessURL": {
            "@required": true,
            "@id": null
        },
        "dcat:mediaType":{
            "@required": false,
            "@type": ["xsd:string"],
            "@value": null
        },
        "dcat:byteSize":{
            "@required": false,
            "@type" : ["xsd:integer"],
            "@value": null
        },
        "dataid:md5Hash":{
            "@required": false,
            "@type" : ["dataid:md5"],
            "@value": null
        },
        "dataid:checksum":{
            "@required": false,
            "@type": ["dataid:crc32", "dataid:md5"],
            "@value": null
        },
        "dataid:graphName":{
            "@required": false,
            "@type":["xsd:string"],
            "@value": null
        },
        "dataid:associatedAgent": {
            "@required": false,
            "@type":["dataid:Agent"],
            "@value": []
        }
    };
    return distr;
}

function getEmptyLinkset(id)
{
    var linkset = {
        "@id": id,
        "@type": ["dataid:Linkset"],
        "@pristine": true,
        "void:objectsTarget":{
            "@required": true,
            "@id":null
        },
        "void:subjectsTarget":{
            "@required": true,
            "@id":null
        },
        "void:triples":{
            "@required": true,
            "@type":["xsd:integer"],
            "@value":null
        },
        "dataid:previousLinksetVersion":{
            "@required": false,
            "@id":null
        },
        "dataid:latestLinksetVersion":{
            "@required": false,
            "@id":null
        },
        "dataid:nextLinksetVersion":{
            "@required": false,
            "@id":null
        },
        "dc:issued": {
            "@required": false,
            "@type": ["xsd:date"],
            "@value": null
        },
        "dc:modified": {
            "@required": false,
            "@type": ["xsd:date"],
            "@value": null
        }
    };
    return linkset;
}

function getEmptyAgent(id, type){
    var agent = {
        "@id": id,
        "@type": ["dataid:Agent"].concat(type),
        "@pristine": true,
        "dataid:agentMail": {
            "@required": true,
            "@type":["xsd:string"],
            "@value": null
        },
        "rdfs:label": {
            "@required": false,
            "@language": null,
            "@value": {}
        },
        "dataid:agentName": {
            "@required": true,
            "@type":["xsd:string"],
            "@value": id.substring(Math.max(id.lastIndexOf('#')+1, id.lastIndexOf('/')+1))
        },
        "dataid:agentUrl" : {
            "@required": false,
            "@type":["xsd:string"],
            "@value": null
        },
        "dataid:agentRole" : {
            "@required": false,
            "@type":["xsd:string"],
            "@value": type.length > 0 ? type[0].substring(type[0].indexOf(':')+1) : 'Agent'
        }
    }
    return agent;
}