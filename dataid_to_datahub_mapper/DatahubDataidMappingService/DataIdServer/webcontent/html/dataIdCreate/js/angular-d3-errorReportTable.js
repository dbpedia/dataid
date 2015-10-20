/**
 * Created by Chile on 9/14/2015.
 */

function tabulate(table, columns, data) {
    table.append("table")
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