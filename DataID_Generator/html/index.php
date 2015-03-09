<?php include 'header.php'; ?>

<div class="container">
    <div>
        <?php include './ng_scripts.php'; ?> 
        <div class="form-group">
            <div class="row">
                <br>
                <div class="col-lg-12">
                    <h2 class="page-header">Dataset: {{ds.title}}</h2>
                </div>
                <span  style="color: red;">*</span> Required Field 
                <br> 
                <br>
                <div class="col-md-5">
                    <?php include './version.php'; ?>

                    <?php include './general_information.php'; ?>
                    <?php include './description_rights.php'; ?>

                </div>
                <div class="col-md-5">
                    <?php include './contact.php'; ?>
                    <?php include './prov.php'; ?>
                </div>
                <div class="col-md-10">
                    <?php include './distributions.php'; ?>
                    <?php include './linkset.php'; ?>
                </div>

            </div>
            <div>
            </div>        
        </div>
    </div>
    <div id="rdfOutput" style="display: none">
        <a class="label label-success ng-binding"  href="" ng-click="downloadInnerHtml()">Download DataID file</a>
        <pre id="rdf" style="margin: 0.5em; padding:0.5em; background-color:#eee; border:dashed 1px grey;">
{{rdf}}
        </pre>
    </div>
    <div id="rdfOutputValidator">
        <div id="rdfOutputValidatorHTML">


        </div>
    </div>
    <?php include 'footer.php'; ?>