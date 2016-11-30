"use strict";

$(document).ready(function() {

    const baseURL = "/NatureOfSlovakia/api/";
    const searchURL = "https://www.google.com/search?tbm=isch&q=";

    mapboxgl.accessToken = 'pk.eyJ1Ijoia293ZWkiLCJhIjoiY2l2cHc2anZqMDAxeDJva3djYjlicHExOSJ9.7kswi8cdOAmRVXh8NJKsBQ';
    var map = new mapboxgl.Map({
        container: "map",
        style: "mapbox://styles/kowei/ciw2otjsy00gq2klkabbozv2e",
        center: [19.5, 48.8],
        zoom: 7.5
    });

    map.addControl(new mapboxgl.GeolocateControl());

    var totalSizes = {};
    var trees = [];

    if (!Array.prototype.last) {
        Array.prototype.last = function() {
            return this[this.length - 1];
        };
    }

    map.on("load", function() {
        $.getJSON(baseURL + "trees", function(data) {
            $.each(data, function(i, tree) {
                var feature = {
                    "type" : "Feature",
                    "geometry" : tree.geometry,
                    "properties": tree.properties
                };
                trees.push(feature);
            });
        });

        $.getJSON(baseURL + "areas/totalSizes", function(data) {
            console.log("fcs dt", data);
            $.each(data, function(i, row) {
                console.log("row:", row);
                var rw = JSON.parse(row);
                totalSizes[rw.name] = rw.totalsize;
            });
        });
    });

    $.getJSON(baseURL + "areas", function(data) {
       var features = [];
       $.each(data, function(i, protectedArea) {
           var feature = {
               "type" : "Feature",
               "geometry" : protectedArea.geometry,
               "properties": protectedArea.properties
           };
           features.push(feature);
       });
       drawAreas(features);
       drawTrees(trees);
   });

    function drawTrees(features) {
        map.addSource("trees", {
            "type": "geojson",
            "data": {
                "type": "FeatureCollection",
                "features": features
            }
        });

        map.addLayer({
            "id": "treesLayer",
            "type": "symbol",
            "source": "trees",
            "layout": {
                "icon-image": "park-15",
                "text-field": "{name}",
                "text-font": ["Open Sans Semibold", "Arial Unicode MS Bold"],
                "text-offset": [0, 0.3],
                "text-size": 12,
                "text-anchor": "top"
            }
        }, "largeAreas");
    }

    function drawAreas(features) {
        map.addSource("largeAreas", {
            "type": "geojson",
            "data": {
                "type": "FeatureCollection",
                "features": features
            }
        });

        map.addLayer({
            "id": "zoneA",
            "type": "fill",
            "source": "largeAreas",
            "layout": {},
            "paint": {
                "fill-color": "#DF0002",
                "fill-opacity": 0.7
            },
            "filter": ["==", "zone", "Zóna A - V. stupeň ochrany"]
        });

        map.addLayer({
            "id": "bordersA",
            "type": "line",
            "source": "largeAreas",
            "layout": {},
            "paint": {
                "line-color": "#8F0013",
                "line-width": 1
            },
            "filter": ["==", "zone", "Zóna A - V. stupeň ochrany"]
        });

        map.addLayer({
            "id": "zoneB",
            "type": "fill",
            "source": "largeAreas",
            "layout": {},
            "paint": {
                "fill-color": "#df890b",
                "fill-opacity": 0.7
            },
            "filter": ["==", "zone", "Zóna B - IV. stupeň ochrany"]
        });

        map.addLayer({
            "id": "bordersB",
            "type": "line",
            "source": "largeAreas",
            "layout": {},
            "paint": {
                "line-color": "#df711b",
                "line-width": 1
            },
            "filter": ["==", "zone", "Zóna B - IV. stupeň ochrany"]
        });

        map.addLayer({
            "id": "zoneC",
            "type": "fill",
            "source": "largeAreas",
            "layout": {},
            "paint": {
                "fill-color": "#dfd61f",
                "fill-opacity": 0.5
            },
            "filter": ["==", "zone", "Zóna C - III. stupeň ochrany"]
        });

        map.addLayer({
            "id": "bordersC",
            "type": "line",
            "source": "largeAreas",
            "layout": {},
            "paint": {
                "line-color": "#FFD914",
                "line-width": 1
            },
            "filter": ["==", "zone", "Zóna C - III. stupeň ochrany"]
        });

        map.addLayer({
            "id": "zoneD",
            "type": "fill",
            "source": "largeAreas",
            "layout": {},
            "paint": {
                "fill-color": "#088",
                "fill-opacity": 0.5
            },
            "filter": ["==", "zone", "Zóna D - II. stupeň ochrany"]
        });

        map.addLayer({
            "id": "bordersD",
            "type": "line",
            "source": "largeAreas",
            "layout": {},
            "paint": {
                "line-color": "#088",
                "line-width": 1
            },
            "filter": ["==", "zone", "Zóna D - II. stupeň ochrany"]
        });

        map.addLayer({
            "id": "areaHover",
            "type": "fill",
            "source": "largeAreas",
            "layout": {},
            "paint": {
                "fill-color": "#000",
                "fill-opacity": 1
            },
            "filter": ["==", "name", ""]
        });

    }

    map.on("mouseout", function() {
        map.setFilter("areaHover", ["==", "name", ""]);
    });

    map.on("mousemove", function(e) {
        if (!mouseMoveTrees(e.point)) {
            mouseMoveAreas(e.point);
        }
    });

    function mouseMoveTrees(point) {
        var features = map.queryRenderedFeatures(point, { layers: ["treesLayer"] });
        map.getCanvas().style.cursor = (features.length) ? "pointer" : "";
        return features.length;
    }

    function mouseMoveAreas(point) {
        var features = map.queryRenderedFeatures(point, { filter: ["has", "zone"] });

        if (!features) {
            return;
        }

        map.getCanvas().style.cursor = (features.length) ? "pointer" : "";

        if (features.length) {

            map.setFilter("areaHover",
                [
                    "all",
                    ["==", "name", features[0].properties.name],
                    ["==", "zone", features[0].properties.zone]
                ]
            );
        } else {
            map.setFilter("areaHover", ["==", "name", ""]);
        }
    }

    $("select[name='zoneSelect']").change(function() {
        if (this.value == "zoneD") {
            displayZone("D");
            displayZone("C");
            displayZone("B");
            displayZone("A");
        } else if (this.value == "zoneC") {
            hideZone("D");
            displayZone("C");
            displayZone("B");
            displayZone("A");
        } else if (this.value == "zoneB") {
            hideZone("D");
            hideZone("C");
            displayZone("B");
            displayZone("A");
        } else if (this.value == "zoneA") {
            hideZone("D");
            hideZone("C");
            hideZone("B");
            displayZone("A");
        }

        function displayZone(level) {
            map.setLayoutProperty("zone" + level, 'visibility', 'visible');
            map.setLayoutProperty("borders" + level, 'visibility', 'visible');
        }

        function hideZone(level) {
            map.setLayoutProperty("zone" + level, 'visibility', 'none');
            map.setLayoutProperty("borders" + level, 'visibility', 'none');
        }
    });

    var zoneFilters = {
        "zoneA": [
            "all",
            ["==", "zone", "Zóna A - V. stupeň ochrany"],
            ["in", "category", "Národný park", "Chránená krajinná oblasť", "Ochranné pásmo národného parku"]
        ],
        "zoneB": [
            "all",
            ["==", "zone", "Zóna B - IV. stupeň ochrany"],
            ["in", "category", "Národný park", "Chránená krajinná oblasť", "Ochranné pásmo národného parku"]
        ],
        "zoneC": [
            "all",
            ["==", "zone", "Zóna C - III. stupeň ochrany"],
            ["in", "category", "Národný park", "Chránená krajinná oblasť", "Ochranné pásmo národného parku"]
        ],
        "zoneD": [
            "all",
            ["==", "zone", "Zóna D - II. stupeň ochrany"],
            ["in", "category", "Národný park", "Chránená krajinná oblasť", "Ochranné pásmo národného parku"]
        ]
    };

    $(".categoryCheck").change(function () {

        for (var zone in zoneFilters) {
            var categoryFilter = zoneFilters[zone].last();

            if (!this.checked) {
                categoryFilter.splice(categoryFilter.indexOf(this.value), 1);
            } else {
                categoryFilter.push(this.value);
            }

            zoneFilters[zone].pop();
            zoneFilters[zone].push(categoryFilter);

            map.setFilter(zone, zoneFilters[zone]);
            map.setFilter(zone.replace("zone", "borders"), zoneFilters[zone]);
        }

    });


    $("#what-is-nearby").on("click", function() {
        navigator.geolocation.getCurrentPosition(function(position) {
            console.log("FCS POS:", position.coords.latitude, position.coords.longitude);       // todo mko longnitude first

            $.getJSON(baseURL + "areas/nearestFeatures/" + position.coords.latitude + "/" + position.coords.longitude, function(data) {
                var species = $("#species");
                species.empty();
                species.append("<p></p><span class='subtitle'>Najbližšie chránené stromy</span>");

                for (var i = 0; i < 3; i++) {
                    var tree = JSON.parse(data[i]);
                    species.append("<tr><td><a class='treeFlyLink tree' href='#'>" + tree.name + "</a> (" + tree.distance.toFixed(0) + " km)</td></tr>");
                }

                species.append("<p></p><span class='subtitle'>Najbližšie chránené územia</span>");
                for (var i = 3; i < 6; i++) {
                    var area = JSON.parse(data[i]);
                    species.append("<tr><td><a class='parea' href='" + searchURL + area.name + "'>" + area.name + "</a> (" + area.distance.toFixed(0) + " km)</td></tr>");
                }

                species.append("<p></p><span class='subtitle'>Najbližšie druhy</span>");
                for (var i = 6; i < data.length; i++) {
                    var specie = JSON.parse(data[i]);
                    species.append("<tr><td><a target='_blank' href='" + searchURL + specie.name + "'>" + specie.name + "</a></td></tr>");
                }

                $(".treeFlyLink").on("click", function(e) {
                    var tree = getTree(e.target.textContent);

                    map.flyTo({
                        center: [
                            tree.geometry.coordinates[0],
                            tree.geometry.coordinates[1]
                        ]
                    });

                    function getTree(name) {
                        var trx = null;
                        trees.forEach(function(tree) {
                            if (tree.properties.name == name)
                                trx = tree;
                        });
                        return trx;
                    }
                });
            });
        });
    });

    map.on("click", function (e) {
        var features = map.queryRenderedFeatures(e.point, { layers: ["treesLayer"] });

        if (features.length) {
            treeClicked(features);
        } else {
            areaClicked(e.point)
        }

        function treeClicked(features) {
            var props = features[0].properties;

            new mapboxgl.Popup()
                .setLngLat(features[0].geometry.coordinates)
                .setHTML(
                    "<p><b class='treeTitle'>" + props.name + "</b>" + "<img align='right' src='" + props.photo + "'>" + "</p>"
                    + "Slovenský názov: " + props.type + "<br>"
                    + "Vedecký názov: " + props.typelat + "<br>"
                    + "Dátum vyhlásenia: " + props.since + "<br>"
                    + "Právny predpis: " + props.paragraph + "<br>"
                    + "Pôsobnosť: " + props.area + "<br>"
                    + "Katastrálne územie: " + props.cadaster + "<br>"
                    + "<a target='_blank' href='" + props.url + "'> Viac info</a>"
                )
                .addTo(map);
        }

        function areaClicked(point) {
            var features = map.queryRenderedFeatures(point, { filter: ["has", "zone"] });
            if (!features || !features.length) {
                return;
            }

            var feature = features[0];
            var areaName = feature.properties.name;
            var species = $("#species");
            var props = feature.properties;

            buildPopup();

            $.getJSON(baseURL + "species?area=" + areaName, function(data) {
                appendBasicInfo();
                species.append("<p></p><span class='subtitle'>Druhy na území</span>");

                $.each(data, function(i, specie) {
                    var name = specie.properties.name;
                    var plant = specie.properties.plant ? "class = 'plant'" : "";
                    species.append("<tr><td><a " + plant + "target='_blank' href='" + searchURL + name + "'>" + name + "</a></td></tr>");
                });

                $.getJSON(baseURL + "trees?area=" + areaName, function(trees) {
                    species.append("<p></p><span class='subtitle'>Chránené stromy</span>");
                    if (!trees.length) {
                        species.append("<tr><td><a href='#' class='tree'>Žiadne</a></td></tr>");
                    }
                    $.each(trees, function(i, tree) {
                        var name = tree.properties.name;
                        species.append("<tr><td><a target='_blank' class='tree' href='" + tree.properties.url + "'>" + name + "</a></td></tr>");
                    });
                });

            });

            function appendBasicInfo() {
                species.empty();
                species.append("<p><b>" + areaName + "</b></p>");
                species.append("<tr><td><span class='subtitle'>Celková výmera </span><br>" + totalSizes[areaName] + " km<sup>2</sup></td></tr>");
            }

            function buildPopup() {
                new mapboxgl.Popup()
                    .setLngLat(map.unproject(e.point))
                    .setHTML(
                        "<p><b>" + areaName + "</b><br>"
                        + props.zone + "</p>"
                        + "Výmera: " + props.size + " km<sup>2</sup><br>"
                        + "Kategória: " + props.category + "<br>"
                        + "Dátum vyhlásenia: " + props.since + "<br>"
                        + "<a target='_blank' href='" + props.url + "'> Viac info</a>"
                    )
                    .addTo(map);
            }
        }
    });

});