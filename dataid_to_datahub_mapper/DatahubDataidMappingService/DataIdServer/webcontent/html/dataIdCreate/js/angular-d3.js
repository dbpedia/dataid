angular.module('d3', [])
    .factory('d3Service', [function(){
        var canvas = d3.select(ele[0]);
        var renderTimeout;
        var margin = parseInt(attrs.margin) || 20,
            barHeight = parseInt(attrs.barHeight) || 20,
            barPadding = parseInt(attrs.barPadding) || 5;


        scope.$watch('validationResult', function(newData) {
            alert(JSON.stringify(newData));
            scope.render(newData);
        }, true);

        /*                   $window.onresize = function() {
         scope.$apply();
         };

         scope.$watch(function() {
         return angular.element($window)[0].innerWidth;
         }, function() {
         scope.render(scope.data);
         });*/


        function render(data) {
            var layers = getLayers(data);
            var counts = {};
            for (var i = 0; i < layers.length; i++) {
                var num = layers[i]["level"];
                counts[num] = counts[num] ? counts[num] + 1 : 1;
            }
            var peopleTable = tabulate(canvas, ["level", "resource", "message"], layers);

            resultTitle("Validation result: number of tests: " + result["@graph"][0]["testsRun"] + ", number of errors: " +
            counts["rlog:ERROR"] != null ? counts["rlog:ERROR"] : 0 + ", number of warnings: " +
            counts["rlog:WARN"] != null ? counts["rlog:WARN"] : 0);
            // uppercase the column headers
            peopleTable.selectAll("thead th")
                .text(function (column) {
                    return column.charAt(0).toUpperCase() + column.substr(1);
                })
                .style("background-color", function (d) {
                    return "#B2D1E0";
                });
            peopleTable.selectAll("tbody td")
                .text(function (td) {
                    if (td.column == "level")
                        return td.value.replace('rlog:', '').replace('WARN', 'WARNING');
                    else
                        return td.value;
                });

            peopleTable.selectAll("tbody tr")
                .style("background-color", function (d) {
                    //d2 will be the same as d, but j will always be 0
                    //since d3.select(this) only has one element
                    if (d["level"] == "rlog:ERROR") {
                        return "#FF5C33";
                    }
                    else if (d["level"] == "rlog:WARN") {
                        return "#FFD633";
                    }
                    else {
                        return "#ffffff";
                    }
                });

            $('table').DataTable({
                "order": [[1, "asc"]]
            });
        };
    }]);