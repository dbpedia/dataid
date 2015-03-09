<!--
Script for Distribution Modal
-->

<script type="text/ng-template" id="modalDistContent.html">
    <form name="formDist">
    <div class="modal-header">
    <h3 class="modal-title">New Distribution</h3>

    <div style="padding: 10px 5px;">
    <span> 
    Here you can create a new distribution. 
    </span>
    </div>
    </div>
    <div class="modal-body">
    <div class="row"> 
    <div class="col-md-6">

    <label>Property type: <span style="color: red">*</span> </label> <br/> 
    <input type="radio" ng-model="distribution.prop" value="void:sparqlEndpoint"> void:sparqlEndpoint 
    <input style="margin-left: 20px" type="radio" ng-model="distribution.prop" value="dcat:distribution"> dcat:distribution <br/>

    <label style="margin-top: 10px" for="dTitle">Title</label> <span style="color: red">*</span>
    <a style="margin-top: 10px"  class="desc2" target="_blank" href='http://www.w3.org/TR/vocab-dcat/#Property:catalog_title'> dct:title </a>    
    <br> 
    <input class="input" size="57" type="text" id="dTitle" ng-model="distribution.title"> <br>

    <label for="dDescription">Description</label> 
    <a class="desc2" target="_blank" href='http://www.w3.org/TR/vocab-dcat/#Property:dataset_description'> dct:description </a>   
    <br>
    <textarea class="input" rows="4" id="dDescription" ng-model="distribution.description"></textarea> <br>

    <label for="dAccessURL">Access URL</label>
    <a style="float: right; margin-right: 0px;" target="_blank" href='http://www.w3.org/TR/vocab-dcat/#Property:distribution_accessurl'> dcat:acessUrl </a>   
    <br>
    <input url class="input" type="text" id="dAccessURL" name="dAccessURL" ng-model="distribution.accessUrl"> <br>
    <span ng-show="formDist.dAccessURL.$error.url">{{messages.notValidURI}} <br></span>

    <label for="">Format</label>
    <a style="float: right; margin-right: 0px;" target="_blank" href='http://dublincore.org/documents/2012/06/14/dcmi-terms/?v=terms#format'> dct:format </a>   
    <br>
    <input  class="input"  type="text" id="" ng-model="distribution.format"> <br>
    </div>
    <div class="col-md-5">
    <div class="row">
    <div class="col-md-5">

    <label for="dIssued">Issued</label>
    <a style="float: right; margin-right: 0px;" target="_blank" href='http://www.w3.org/TR/vocab-dcat/#Property:catalog_release_date'> dct:issued </a>   
    <br>
    <input placeholder="mm-dd-yyyy" size="13" type="text" id="dIssued" ng-model="distribution.issued"> <br>
    </div>
    <div class="col-md-5">
    </div>
    </div>

    <label for="dTriples">Triples</label>
    <a style="float: right; margin-right: 0px;" target="_blank" href='http://www.w3.org/TR/void/'> void:triples </a>   
    <br>
    <input class="input" type="text" id="dTriples" name="dTriples" ng-model="distribution.triples"> <br>

    <label for="dRights">Rights</label>
    <a class="desc2" target="_blank" href='http://www.w3.org/TR/vocab-dcat/#Property:catalog_rights'> dct:rights </a>    
    <br>
    <textarea class="input" rows="4" id="dRights" ng-model="distribution.rights"></textarea> <br>

    <label for="">Media Type</label>
    <a style="float: right; margin-right: 0px;" target="_blank" href='http://www.w3.org/TR/vocab-dcat/#Property:distribution_media_type'> dcat:mediaType </a>   
    <br>
    <input class="input" type="text" id="" ng-model="distribution.mediaType"> <br>

    </div>
    </div>
    <div class="modal-footer">
    <button class="btn btn-primary" ng-click="ok()">OK</button>
    <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
    </div>
    </form>
</script>



<!--
Script for Linkset Modal
-->

<script type="text/ng-template" id="modalLinksetContent.html">
    <form name="formLinkset" novalidate>
    <div class="modal-header">
    <h3 class="modal-title">New Linkset</h3>
    <div style="padding: 10px 5px;">
    <span> 
    Here you can create a new linkset. 
    </span>
    </div>
    </div>
    <div class="modal-body">
    <div class="row">
    <div class="col-md-10">
    <label>Property type: <span style="color: red">*</span> </label><br>
    <input type="radio" ng-model="linkset.prop" value="void:subset"> void:subset 
    <input style="margin-left: 20px" type="radio" ng-model="linkset.prop" value="dataid:containsLink"> dataid:containsLink <br/>

    <label style="margin-top: 10px" for="linksetIssued">Issued</label>
    <a style="margin-top: 10px; float: right; margin-right: 0px;" target="_blank" href='http://www.w3.org/TR/vocab-dcat/#Property:catalog_release_date'> dct:issued </a>   
    <br>
    <input class="input" type="text" id="linksetIssued" ng-model="linkset.issued"> <br>

    <label for="linksetModified">Modified</label>
    <a style="float: right; margin-right: 0px;" target="_blank" href=' http://www.w3.org/TR/vocab-dcat/#Property:catalog_update_date'> dct:modified </a>   
    <br>
    <input class="input" type="text" id="linksetModified" ng-model="linkset.modified"> <br>

    <label for="linksetExampleResource">Example Resource</label>
    <a class="desc2" target="_blank" href='http://www.w3.org/TR/void/#example-resource'> void:exampleResource </a>   
    <br>
    <input url class="input" type="text" id="linksetExampleResource" name="linksetExampleResource" ng-model="linkset.exampleResource"> <br>
    <span ng-show="formLinkset.linksetExampleResource.$error.url">{{messages.notValidURI}} <br></span>

    <label for="linksetLinkPredicate">Link Predicate</label>
    <a class="desc2" target="_blank" href='http://vocab.deri.ie/void#linkPredicate'> void:linkPredicate </a>  
    <br>
    <input class="input" type="text" id="linksetLinkPredicate" ng-model="linkset.linkPredicate"> <br>

    <label for="linksetTriples">Triples</label>
    <a style="float: right; margin-right: 0px;" target="_blank" href='http://www.w3.org/TR/void/'> void:triples </a>  
    <br>
    <input class="input" type="text" id="linksetTriples" ng-model="linkset.triples"> <br>
    <label for="linksetTarget">Target</label><a class="desc2" target="_blank" href='http://vocab.deri.ie/void#target'> void:target </a>  
    <br>
    <input class="input" type="text" id="linksetTarget" ng-model="linkset.target"> <br>
    </div>
    </div>
    <div class="modal-footer">
    <button class="btn btn-primary" ng-click="ok()">OK</button>
    <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
    </div>
    </form>
</script>

<!--
Template for contact information modal
-->

<script type="text/ng-template" id="modalAgentContent.html">
    <form name="formLinkset" novalidate>
    <div class="modal-header">
    <h3 class="modal-title">New Contact Information</h3>
    <div style="padding: 10px 5px;">
    <span> 
    Here you can create new contact information. 
    </span>
    </div>
    </div>
    <div class="modal-body">
    <div class="row">
    <div class="col-md-11">
    <label>Property type: <span style="color: red">*</span> </label> <br/> 
    <input type="radio" ng-model="agent.prop" value="dcat:contactPoint" > dcat:contactPoint 
    <input style="margin-left: 20px" type="radio" ng-model="agent.prop" value="dct:publisher"> dct:publisher 
    <input style="margin-left: 20px" type="radio" ng-model="agent.prop" value="dct:creator"> dct:creator 
    <input style="margin-left: 20px" type="radio" ng-model="agent.prop" value="dct:contributor"> dct:contributor <br/>

 <!--   <label style="margin-top: 10px">Resource</label> 
    <input class="input-modal-md"  placeholder="Contact Point Resource" ng-model="agent.resource"> <br/> -->
    <label style="margin-top: 10px">Label</label> 
    <a style="margin-top: 10px" class="desc2" target="_blank" href='http://www.w3.org/TR/rdf-schema/#ch_label'> rdfs:label </a>    <br/> 
    <input class="input-modal-md"  placeholder="Contact Point Label" ng-model="agent.label"> <br/>

    <label>Name</label> 
    <a class="desc2" target="_blank" href='http://xmlns.com/foaf/spec/#term_name'> foaf:name </a>    
    <br/> 
    <input class="input-modal-md"  placeholder="Contact Point Name"  ng-model="agent.name"> <br/>
    <label>Mailbox</label> 
    <a class="desc2" target="_blank" href='http://xmlns.com/foaf/spec/#term_mbox'> foaf:mbox </a>    
    <br/> 
    <input class="input-modal-md" placeholder="Contact Point Mailbox" ng-model="agent.mbox"> <br/>
    <br/>
    </div>
    </div>
    <div class="modal-footer">
    <button class="btn btn-primary" ng-click="ok()">OK</button>
    <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
    </div>
    </form>
</script>

<!--
Template for wasDerivedFrom modal
-->

<script type="text/ng-template" id="modalWasDerivedFromAgentContent.html">
    <form name="WasDerivedFrom" novalidate>
    <div class="modal-header">
    <h3 class="modal-title">New WasDerivedFrom Agent</h3>
    <div style="padding: 10px 5px;">
    <span> 
    Here you can create new WasDerivedFrom contact information. 
    </span>
    </div>
    </div>
    <div class="modal-body">
    <div class="row">
    <div class="col-md-10">
    <input type="radio" ng-model="wasDerivedFromAgent.prop" value="dct:creator"> dct:creator 
    <input style="margin-left:20px" type="radio" ng-model="wasDerivedFromAgent.prop" value="dct:contributor"> dct:contributor 
    <input style="margin-left:20px"  type="radio" ng-model="wasDerivedFromAgent.prop" value="prov:wasAttributedTo"> prov:wasAttributedTo <br/>

    <label style="margin-top:10px">Label <span style="color: red">*</span> </label> 
    <a style="margin-top:10px" class="desc2" target="_blank" href='http://www.w3.org/TR/rdf-schema/#ch_label'> rdfs:label </a>    
    <br/> 
    <input  class="input"  placeholder="WasDerivedFrom Contact Point Label" ng-model="wasDerivedFromAgent.label"> <br/>

    <label>Name <span style="color: red">*</span> </label> 
    <a class="desc2" target="_blank" href='http://xmlns.com/foaf/spec/#term_name'> foaf:name </a>    
    <br/> 
    <input  class="input"  placeholder="WasDerivedFrom Contact Point Name"  ng-model="wasDerivedFromAgent.name"> <br/>

    <label>Mailbox <span style="color: red">*</span> </label> 
    <a class="desc2" target="_blank" href='http://xmlns.com/foaf/spec/#term_mbox'> foaf:mbox </a>    
    <br/> 
    <input class="input" placeholder="WasDerivedFrom Contact Point Mailbox" ng-model="wasDerivedFromAgent.mbox"> <br/>
    <br/>
    </div>
    </div>
    <div class="modal-footer">
    <button class="btn btn-primary" ng-click="ok()">OK</button>
    <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
    </div>
    </form>
</script>

<!--
Template for wasGeneratedBy modal
-->

<script type="text/ng-template" id="modalWasGeneratedByAgentContent.html">
    <form name="formLinkset" novalidate>
    <div class="modal-header">
    <h3 class="modal-title">New WasGeneratedBy Contact Information</h3>
    <div style="padding: 10px 5px;">
    <span> 
    Here you can create new WasGeneratedBy contact information. 
    </span>
    </div>
    </div>
    <div class="modal-body">
    <div class="row">
    <div class="col-md-10">

    <input type="radio" ng-model="wasGeneratedByAgent.prop" value="dct:creator"> dct:creator 
    <input style="margin-left: 20px" type="radio" ng-model="wasGeneratedByAgent.prop" value="dct:contributor"> dct:contributor 
    <input style="margin-left: 20px" type="radio" ng-model="wasGeneratedByAgent.prop" value="prov:wasAssociatedWith"> prov:wasAssociatedWith <br/>

    <label style="margin-top: 10px">Label <span style="color: red">*</span> </label> 
    <a style="margin-top: 10px" class="desc2" target="_blank" href='http://www.w3.org/TR/rdf-schema/#ch_label'> rdfs:label </a>    
    <br/> 
    <input  class="input"  placeholder="Contact Point Label" ng-model="wasGeneratedByAgent.label"> <br/>
    <label>Name <span style="color: red">*</span> </label> 
    <a class="desc2" target="_blank" href='http://xmlns.com/foaf/spec/#term_name'> foaf:name </a>    
    <br/> 
    <input  class="input"  placeholder="Contact Point Name"  ng-model="wasGeneratedByAgent.name"> <br/>
    <label>Mailbox <span style="color: red">*</span> </label> 
    <a class="desc2" target="_blank" href='http://xmlns.com/foaf/spec/#term_mbox'> foaf:mbox </a>    
    <br/> 
    <input class="input" placeholder="Contact Point Mailbox" ng-model="wasGeneratedByAgent.mbox"> <br/>
    <br/>
    </div>
    </div>
    <div class="modal-footer">
    <button class="btn btn-primary" ng-click="ok()">OK</button>
    <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
    </div>
    </form>
</script>



<script type="text/ng-template" id="modalUploadDataHub.html">
    <form name="formSend" novalidate>
    <div class="modal-header">
    <h3 class="modal-title">Upload to Datahub</h3>
    </div>
    <div class="modal-body">

    <label for="key">API key</label> <br>
    <input class="input" type="text" id="key" ng-model="dataHubObj.key"> <br>
    <label for="org">Organization</label><br>
    <input class="input" type="text"  name="org" ng-model="dataHubObj.org"> <br>
    <label for="login">Login Name</label><br>
    <input class="input" rows="4" name="login" ng-model="dataHubObj.login"> <br>
    <label for="url">URL</label><br>
    <input url class="input" type="text" id="url" name="url" ng-model="dataHubObj.url"> <br>
    <span ng-show="formDist.url.$error.url">{{messages.notValidURI}} <br></span>

    </div>
    <div class="modal-footer">
    <button class="btn btn-primary" ng-click="ok()">OK</button>
    <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
    </div>
    </form>
</script>

<script type="text/ng-template" id="modalDistWebContent.html">
    <div class="modal-header">
    <h3 class="modal-title">Add Distributions from Apache's Web List</h3>
    <div style="padding: 10px 5px;">
    <span> 
    If your dataset has a list of distributions like <a target="_blank" href="/parser/ex">this example</a>, answer some questions and the generator will create a distribution list for you. Please, make sure that your files are not located in subfolders, otherwise, the parser will ignore them.
    </span>
    </div>
    </div>
    <div class="modal-body">
    <label>What is the address that the distributions are located?</label><br>
    <input  class="input" rows="4" ng-model="address2"> <br>

    <label>What is the media type format of the files? (e.g. text/plain)</label><br>
    <input  class="input" rows="4" ng-model="mediaType"> <br>

    <label>Enter here the description for distributions:</label><br>
    <input  class="input" rows="4" ng-model="description"> <br>

    </div>
    <div class="modal-footer">
    <button class="btn btn-primary" ng-click="ok(address2,mediaType,description)">Parse!</button>
    <button class="btn btn-warning" ng-click="cancel()">Cancel</button>
    </div>
</script>


<script type="text/ng-template"  id="tree_item_renderer.html">
    <div ng-if="dataSet.type=='void:subset'">
    <a class="label label-xlg label-info" href="" ng-click="changeSubset(dataSet)">{{dataSet.title}}</a><div class="pull-right"> <a class="label label-xlg label-danger" href="" ng-click="delDataset2(dataSet)" >-</a><a style="margin-left:1px;" class="label label-xlg label-success" href=""  ng-click="addDataset2(dataSet)">+</a></div>
    </div>
    <div ng-if="dataSet.type=='main'">
    <a class="label label-xlg label-success" href="" ng-click="changeSubset(dataSet)">{{dataSet.title}}</a><div class="pull-right"> <a class="label label-xlg label-success" href=""  ng-click="addDataset2(dataSet)">+</a></div>
    </div>
    <div ng-if="dataSet.type=='dataid:version'">
    <a class="label label-xlg label-warning" href="" ng-click="changeSubset(dataSet)">{{dataSet.title}}</a><div class="pull-right"> <a class="label label-xlg label-danger" href="" ng-click="delDataset2(dataSet)" >-</a><a style="margin-left:1px;"  class="label label-xlg label-success" href=""  ng-click="addDataset2(dataSet)">+</a></div>
    </div>
    <div ng-if="dataSet.type=='dataid:latestVersion'">
    <a class="label label-xlg label-default" href="" ng-click="changeSubset(dataSet)">{{dataSet.title}}</a><div class="pull-right"> <a class="label label-xlg label-danger" href="" ng-click="delDataset2(dataSet)" >-</a><a style="margin-left:1px;"  class="label label-xlg label-success" href=""  ng-click="addDataset2(dataSet)">+</a></div>
    </div>
    <ul> 
    <li ng-repeat="dataSet in dataSet.datasets" ng-include="'tree_item_renderer.html'"></li>
    </ul>
</script>
