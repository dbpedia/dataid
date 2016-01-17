/**
 * Created by Chile on 8/19/2015.
 */
var request = null;
function validate() {
    document.getElementById('canvas').innerHTML = "";
    var agentUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '')+'/dataid/graph/validateid';
    var method = "POST";
    var postData = document.getElementById('dataid').value;
    var response = JSON.parse(sendRequest(agentUrl, method, postData, false, function () { }, function () {}));

        var layers = getLayers(response);
        var counts = {};
        for(var i = 0; i< layers.length; i++) {
            var num = layers[i]["level"];
            counts[num] = counts[num] ? counts[num]+1 : 1;
        }
        var peopleTable = tabulate(["level","resource", "message"], response);

        resultTitle("Validation result: number of tests: " + response["@graph"][0]["testsRun"] + ", number of errors: " +
        counts["rlog:ERROR"] != null ? counts["rlog:ERROR"] : 0 + ", number of warnings: " +
        counts["rlog:WARN"] != null ? counts["rlog:WARN"] : 0);
        // uppercase the column headers
        peopleTable.selectAll("thead th")
            .text(function (column) {
                return column.charAt(0).toUpperCase() + column.substr(1);
            })
            .style("background-color", function(d) {
                return "#B2D1E0";
            });
        peopleTable.selectAll("tbody td")
            .text(function (td) {
                if(td.column == "level")
                    return td.value.replace('rlog:', '').replace('WARN', 'WARNING');
                else
                    return td.value;
            });

        peopleTable.selectAll("tbody tr")
            .style("background-color", function(d) {
                //d2 will be the same as d, but j will always be 0
                //since d3.select(this) only has one element
                if (d["level"] == "rlog:ERROR"){
                    return "#FF5C33";
                }
                else if (d["level"] == "rlog:WARN"){
                    return "#FFD633";
                }
                else {
                    return "#ffffff" ;
                }
            } );

        $('table').DataTable({
            "order": [[ 1, "asc" ]]
        });
}

function resultTitle(title)
{
    document.getElementById('resulttitle').innerHTML = title;
}

function tabulate(columns, inp) {
    var table = d3.select("#canvas")
            .append("table")
            .attr("border","1")
            .attr("id","table")
            .attr("class","display dataTable")
            .attr("width","100%")
            .attr("cellspacing","0"),
        thead = table.append("thead"),
        tfoot = table.append("tfoot"),
        tbody = table.append("tbody")
            .attr("overflow-y", "auto")
            .attr("overflow-x", "hidden");

    //columns.unshift("language");
    var data = getLayers(inp);
    //append the header row
    if(columns == null)
    {
        columns = [];
        for (var property in data[0]) {
            columns.push(property);
        };
    }
    thead.append("tr")
        .selectAll("th")
        .data(columns)
        .enter()
        .append("th")
        .attr("bgcolor","#FFFFFF")
        .text(function(g) { return g; });

    // create a row for each object in the data
    var rows = tbody.selectAll("tr")
        .data(data)
        .enter()
        .append("tr");

    // create a cell in each row for each columnstatObj[lang]
    var cells = rows.selectAll("td")
        .data(function(row) {
            return columns.map(function(column) {
                return { lang:row['language'], column: column, value: row[column]};
            });
        })
        .enter()
        .append("td")
        .text(function(d) {  return d.value; });


    return table;
}

function formatInput()
{
    var format = document.getElementById('serialSelect').options[select.selectedIndex].value;
    var input = document.getElementById('dataid').value;
    document.getElementById('dataid').value = prettify(input, format);
}

function getLayers(inp) {
    if(inp == null)
        inp = JSON.parse(document.getElementById( 'data' ).textContent);
    var ret = [];
    for(var i = 0; i < inp["@graph"].length; i++)
    {
        if(inp["@graph"][i]["level"] != null)
        {
            ret.push(inp["@graph"][i]);
        }
    }
    return ret;
}

