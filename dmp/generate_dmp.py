# -*- coding: utf-8 -*-
import sys, getopt, os, collections, csv, rdflib
from rdflib import Graph, URIRef, Literal, plugin
from rdflib.namespace import Namespace
from rdflib.plugins.parsers.notation3 import BadSyntax

def queryAuthors(graph, namespaces):
    qres = graph.query("""SELECT DISTINCT ?role ?name ?email 
        WHERE {
          ?dataset a dataid:Dataset . FILTER NOT EXISTS {?o void:subset ?dataset }
          ?dataset dataid:associatedAgent ?agent .
          ?agent rdf:type ?role ;
                 dataid:agentName ?name ;
                 dataid:agentMail ?email .         
        }""",initNs=namespaces)

    if len(qres.bindings) == 0:
        print("[ERROR] No authors found, author information is mandatory", file=sys.stderr)
    authors = []
    count = 0
    for row in qres.bindings:
        authors.append(dict()) 
        authors[count]["role"] = row["?role"][row["?role"].find("#")+1:]
        authors[count]["name"] = row["?name"]
        authors[count]["email"] = row["?email"]
        count += 1
    return authors

def getProv(graph, namespaces):
    qres = graph.query("""SELECT DISTINCT ?entityname ?page ?attributeName ?email
        WHERE {
          ?dataset a dataid:Dataset . FILTER NOT EXISTS {?o void:subset ?dataset }
          ?dataset prov:wasDerivedFrom ?entity .
          ?entity dc:title ?entityname ;
                  foaf:homepage ?page .
           OPTIONAL{?entity prov:wasAttributedTo ?agent. 
                    ?agent foaf:name ?attributeName.
                    ?agent foaf:mbox ?email}
        }""",initNs=namespaces)
    
    if len(qres.bindings) == 0:
        print("[WARNING] No provenance found, consider adding a prov entity for the data source the dataset was derived from", file=sys.stderr)
        return None
    prov = []
    count = 0
    for row in qres.bindings:         
        prov.append(dict())
        prov[count]["entity"] = str(row["?entityname"])
        prov[count]["page"] = str(row["?page"])
        try:
            prov[count]["by"] = str(row["?attributeName"])
            prov[count]["email"] = str(row["?email"])
        except KeyError:
            print("[WARNING] prov:wasAttributedTo missing, provenance will not be complete", file=sys.stderr)
        count += 1
    return prov

def getDMPStuff(graph, namespaces):
    qres = graph.query("""SELECT DISTINCT ?usefulness ?similarData ?reuseAndIntegration ?additionalSoftware ?repositoryUrl ?growth ?archiveLink ?preservation ?openness ?licenseName
        WHERE {
          ?dataset a dataid:Dataset . FILTER NOT EXISTS {?o void:subset ?dataset }
          OPTIONAL{?dataset dmp:usefulness ?usefulness }
          OPTIONAL{?dataset dmp:similarData ?similarData }
          OPTIONAL{?dataset dmp:reuseAndIntegration ?reuseAndIntegration }
          OPTIONAL{?dataset dmp:additionalSoftware ?additionalSoftware }                 
          OPTIONAL{?dataset dmp:repositoryUrl ?repositoryUrl }
          OPTIONAL{?dataset dmp:growth ?growth }
          OPTIONAL{?dataset dmp:archiveLink ?archiveLink }
          OPTIONAL{?dataset dmp:preservation ?preservation } 
          OPTIONAL{?dataset dmp:openness ?openness }
          OPTIONAL{?dataset dataid:licenseName ?licenseName } 
          
        }""",initNs=namespaces)
           
    dmp = dict()
    count = 0
    if len(qres.bindings) > 1:
        print("[WARNING] Too many DMP properties found, please add only one instance of each property", file=sys.stderr)
    row = qres.bindings[0]
   
    try:
        dmp["usefulness"] = row["?usefulness"]
        dmp["similarData"] = row["?similarData"]
        dmp["reuseAndIntegration"] = row["?reuseAndIntegration"]
        dmp["additionalSoftware"] = row["?additionalSoftware"]
        dmp["repositoryUrl"] = str(row["?repositoryUrl"])
        dmp["growth"] = row["?growth"]
        dmp["archiveLink"] = str(row["?archiveLink"])
        dmp["preservation"] = row["?preservation"]
        dmp["openness"] = row["?openness"]
        dmp["licenseName"] = row["?licenseName"]
    except KeyError:
        print("[ERROR] Some Data Management Plan property is missing, be sure to include them: http://schemas.dbpedia.org/dmp#", file=sys.stderr)
        return None
    
    return dmp

def countLinks(graph, namespaces):
    qres = graph.query("""SELECT DISTINCT ?links  
        WHERE {
          ?dataset a dataid:Dataset . FILTER NOT EXISTS {?o void:subset ?dataset }
          ?dataset void:subset ?sub .
          ?sub a void:LinkSet ;
               void:triples ?triples .
        
        }""",initNs=namespaces)
    
    if len(qres.bindings) == 0:
        print("[WARNING] No linksets found, link size cannot be printed", file=sys.stderr)
    links = 0
    for row in qres.bindings:         
        links+=int(row["?links"])
    return links

def getBasicMetadata(graph, namespaces):
    qres = graph.query("""SELECT DISTINCT ?dataset ?title ?label ?homepage ?desc ?rights ?vocab ?triples ?license
        WHERE {
          ?dataset a dataid:Dataset . FILTER NOT EXISTS {?o void:subset ?dataset }
          ?dataset dc:title ?title;
                   rdfs:label ?label;
                   dcat:landingPage ?homepage;
                   dc:description ?desc .
          OPTIONAL{ ?dataset dc:rights ?rights}
          OPTIONAL{ ?dataset dc:license ?license}
          OPTIONAL{ ?dataset void:vocabulary ?vocab}
          OPTIONAL{ ?dataset void:triples ?triples}  
        }""",initNs=namespaces)
    if len(qres.bindings)==0:
        print("[ERROR] Query returned no results, a required property must be missing", file=sys.stderr)
        return None
    elif len(qres.bindings)>1:
        print("[ERROR] Query returned more than one dataset, make sure there is only one top dataset in your DataID", file=sys.stderr)
        return None

    row = qres.bindings[0]
    data = dict()
    data["dataset"] = str(row["?dataset"])
    data["title"] = row["?title"]
    data["label"] = row["?label"]
    data["homepage"] = str(row["?homepage"])
    data["description"] = row["?desc"]
    data["rights"] = None
    try:
        data["rights"] = row["?rights"]
    except KeyError:
        print("[WARNING] dc:rights missing, citations cannot be printed", file=sys.stderr)
    data["vocab"] = None
    try:
        data["vocab"] = str(row["?vocab"])
    except KeyError:
        print("[WARNING] void:vocabulary missing, ontology cannot be printed", file=sys.stderr)
    data["tripleCount"] = None
    try:
        data["tripleCount"] = row["?triples"]
    except KeyError:
        print("[WARNING] void:triples missing, dataset size cannot be printed", file=sys.stderr)
    data["license"] = None
    try:
        data["license"] = str(row["?license"])
    except KeyError:
        print("[WARNING] dc:license missing, licensing cannot be printed", file=sys.stderr)
    return data

#this does not work yet
#def getLicenseData(license, namespaces):
#    g = Graph()
#    result = g.parse(licenseUrl, format="turtle")
#    result = g.parse("/tmp/rdflicense.ttl", format="turtle")
#    licenseLoc = license
#    querystring = "SELECT DISTINCT ?title WHERE {"+license+" dc:title ?title .}"
#    querystring = """SELECT DISTINCT * WHERE {<http://purl.org/NET/rdflicense/cc-by3.0de> rdfs:label ?title}"""
#    qres = g.query(querystring,initNs=namespaces)
#    if len(qres.bindings)==0:
#        print("[WARNING] could not query license, please include an ODRL license http://oeg-dev.dia.fi.upm.es/licensius/rdflicense/", file=sys.stderr)
#       return None   
#    return str(qres.bindings[0]["?title"])

def generateHtml(data, authors, links, prov, additionalData):
    
    if os.path.exists(data["label"]+".html"):
        os.remove(data["label"]+".html")
    output = open(data["label"]+".html",'a')

#references, authors
    referenceHtml = "<p><span class=\"variable\">Name: </span><span class=\"value\">"+data["title"]+"</span></p>"
    referenceHtml += "<p><span class=\"variable\">Metadata URI: </span><span class=\"value\"><a href=\""+data["dataset"]+"\">"+data["dataset"]+"</a></span></p>"
    referenceHtml += "<p><span class=\"variable\">Homepage: </span><span class=\"value\"><a href=\""+data["homepage"]+"\">"+data["homepage"]+"</a></span></p>"
    for author in authors:
        referenceHtml += "<p><span class=\"variable\">"+author["role"]+": </span><span class=\"value\"><a href=\"mailto:"+author["email"]+"\">"+author["name"]+"</a></span></p>"

#description
    descriptionHtml = "<p><span class=\"variable\">Description: </span><span class=\"value\">"+data["description"]+"</span></p>"

    if prov is not None:
        descriptionHtml += "<p><span class=\"variable\">Provenance: </span>"
        for row in prov:
            descriptionHtml += "<span class=\"value\"><a href="+row["page"]+">"+row["entity"]+"</a></span>"
            try:
                descriptionHtml += " (<a href=\"mailto:"+row["email"]+"\">"+row["by"]+"</a>)"
            except KeyError:
                continue
            descriptionHtml += "</span>"
        descriptionHtml += "</p>"

    if additionalData is not None:
        descriptionHtml += "<p><span class=\"variable\">Usefulness: </span><span class=\"value\">"+additionalData["usefulness"]+"</span></p>"
        descriptionHtml += "<p><span class=\"variable\">Similar data: </span><span class=\"value\">"+additionalData["similarData"]+"</span></p>"
        descriptionHtml += "<p><span class=\"variable\">Re-use and integration: </span><span class=\"value\"><a href=\""+additionalData["reuseAndIntegration"]+"\">"+additionalData["repositoryUrl"]+"</a></span></p>"

#standards
    standardHtml = ""
    if data["vocab"] is not None:
        standardHtml = "<p><span class=\"variable\">Vocabularies and Ontologies used: </span><span class=\"value\">"+data["vocab"]+"</span></p>"
    
#sharing
    sharingHtml = ""
    if additionalData is not None:
        sharingHtml += "<p><span class=\"variable\">License: </span><span class=\"value\">"+additionalData["licenseName"]+"</span></p>"

    if data["license"] is not None:
        sharingHtml += "<p><span class=\"variable\">ODRL license description: </span><span class=\"value\">"+data["license"]+"</span></p>"

    if data["rights"] is not None:
        sharingHtml += "<p><span class=\"variable\">Additional rights description: </span><span class=\"value\">"+data["rights"]+"</span></p>"

    if additionalData is not None:
        sharingHtml += "<p><span class=\"variable\">Openness: </span><span class=\"value\">"+additionalData["openness"]+"</span></p>"
        sharingHtml += "<p><span class=\"variable\">Software necessary: </span><span class=\"value\">"+additionalData["additionalSoftware"]+"</span></p>"
        sharingHtml += "<p><span class=\"variable\">Repository: </span><span class=\"value\"><a href=\""+additionalData["repositoryUrl"]+"\">"+additionalData["repositoryUrl"]+"</a></span></p>"

#preservation
    preserveHtml = ""
    if additionalData is not None:
        preserveHtml += "<p><span class=\"variable\">Preservation: </span><span class=\"value\">"+additionalData["preservation"]+"</span></p>"
        preserveHtml += "<p><span class=\"variable\">Growth: </span><span class=\"value\">"+additionalData["growth"]+"</span></p>"
        preserveHtml += "<p><span class=\"variable\">Archive: </span><span class=\"value\">"+additionalData["archiveLink"]+"</span></p>"

    if data["tripleCount"] is not None:
        preserveHtml += "<p><span class=\"variable\">Size: </span><span class=\"value\">"+data["tripleCount"]+" triples</span></p>"
    if links is not 0:
        preserveHtml += "<p><span class=\"variable\">Links to other datasets: </span><span class=\"value\">"+str(links)+"</span></p>"

    templateString = ""
    with open("dmp_template.html", 'r') as file:
        templateString = file.read()
    templateString = templateString.replace("$Dataset",data["title"])
    templateString = templateString.replace("$Reference",referenceHtml)
    templateString = templateString.replace("$Description",descriptionHtml)
    templateString = templateString.replace("$Standards",standardHtml)
    templateString = templateString.replace("$Sharing",sharingHtml)
    templateString = templateString.replace("$Preservation",preserveHtml)
    output.write(templateString)
    return

def getDataIDs(infile,namespaces):
    g = Graph()
    result = g.parse(infile, format="turtle")    
    qres = g.query("""SELECT DISTINCT ?dataset 
        WHERE {
          ?s dataid:dataIdUrl ?dataset
        }""",initNs=namespaces)
    if len(qres.bindings)==0:
        print("[ERROR] No DataID links found in "+infile, file=sys.stderr)
        return None
    dataids = []
    for row in qres.bindings:
        dataids.append(row["?dataset"])
    return dataids
        
def main(argv):
    infile = ''
    outfile = ''
    try:
        opts, args = getopt.getopt(argv,"hi:",["ifile="])
    except getopt.GetoptError as err:
        print('generate_dmp.py -i <inputfile>')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print('Usage: generate_dmp.py -i <inputfile>')
            sys.exit()
        elif opt in ("-i","--infile"):
            infile = arg
        else:
            print('Usage: generate_dmp.py -i <inputfile>')
            sys.exit()
    
    if os.path.exists(outfile):
        os.remove(outfile)
    
    namespaces = dict(
        dataid=Namespace("http://schema.dbpedia.org/dataid#"),
        void=Namespace("http://rdfs.org/ns/void#"),
        dc=Namespace("http://purl.org/dc/terms/"),
        dcat=Namespace("http://www.w3.org/ns/dcat#"),
        rdf=Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
        foaf=Namespace("http://xmlns.com/foaf/0.1/"),
        prov=Namespace("http://www.w3.org/ns/prov#"),
        dmp=Namespace("http://schema.dbpedia.org/dmp#"))
    
    dataids = getDataIDs(infile, namespaces)
    
    for dataid in dataids:
        g = Graph()
        try:
            result = g.parse(str(dataid), format="turtle")
            data = getBasicMetadata(g, namespaces)
            if data is None:
                continue

            authors = queryAuthors(g, namespaces)
            if len(authors)==0:
                continue

            links = countLinks(g, namespaces)
            prov = getProv(g, namespaces)     
            additionalData = getDMPStuff(g, namespaces)
            generateHtml(data, authors, links, prov, additionalData)
        except BadSyntax:
            print("[ERROR] Could not parse "+str(dataid), file=sys.stderr)
        

if __name__ == "__main__":
    main(sys.argv[1:])
