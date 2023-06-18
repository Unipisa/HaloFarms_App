package it.unipi.halofarms.screen.zone
/*
    /**
     * Build the invisible squares inside perimeter which center contains suggested point.
     * For drawing that cells, it firstly calculate minimum rectangle containing perimeter,
     * then build the cells moving a square inside this rectangle until it isn't fill.
     *
     * @param coordinates Coordinates of the perimeter
     */
    private fun drawGrid(coordinates: List<LatLng>) {
        /* Gets all the perimeter's points */
        val builder: LatLngBounds.Builder = LatLngBounds.builder()
        for (latLng in coordinates) {
            builder.include(latLng)
        }

        //------ THIS IS THE MINIMUM RECTANGLE THAT CONTAINS MY PERIMETER ------
        /* Gets the top-right vertex */
        val northeast: LatLng = builder.build().northeast
        /* Gets the bottom left vertex */
        val southwest: LatLng = builder.build().southwest
        /* Gets top-left vertex */
        val northwest = LatLng(northeast.latitude, southwest.longitude)
        /* Gets bottom-right vertex */
        val southeast = LatLng(southwest.latitude, northeast.longitude)
        /* Minimum rectangle that contains perimeterPoly: for drawing suggested point */
        val rectanglePoly: Polygon = googleMap.addPolygon(PolygonOptions()
            .visible(false)
            .add(northwest)
            .add(northeast)
            .add(southeast)
            .add(southwest))
        /* Calculates area of internal cells */
        val meters = areaOfSquare()
        /* Number of default iterations to fill the minimum rectangle */
        val iterations = if (area > 4) 50 else 20
        /* Builds the grid */buildInternalCells(meters, iterations, southeast, rectanglePoly)
    }



    /**
     * Builds the grid of the field: every center of internal cells is the suggested point.
     *
     * @param meters Area of every cell
     * @param iterations Number of image-moving inside rectangle
     * @param startPoint Point from whom image-moving starts
     * @param rectangle Minimum rectangle containing perimeter
     */
    private fun buildInternalCells(
        meters: Float, iterations: Int, startPoint: LatLng,
        rectangle: Polygon
    ) {
        /* Starts to build the internal squares:
           use an image of square for every cell and drawn on top of it a polygon
           first square-image: start from bottom-right corner */
        var southeast: LatLng? = startPoint
        var northeast: LatLng
        var southwest: LatLng
        var northwest: LatLng?
        val options: GroundOverlayOptions = GroundOverlayOptions()
            .visible(false)
            .image(Objects.requireNonNull(bitmapDescriptorFromVector(R.drawable.square)))
            .anchor(0, 0)
            .position(southeast, meters)
        var prev: GroundOverlay?
        var curr: GroundOverlay = googleMap.addGroundOverlay(options)

        /* Moves in horizontal */for (i in 0 until iterations) {
            prev = curr
            /* Moves in vertical */for (j in 0 until iterations) {
                /* Builds new square-image */
                assert(curr != null)
                curr = googleMap.addGroundOverlay(GroundOverlayOptions()
                    .visible(false)
                    .image(Objects.requireNonNull(bitmapDescriptorFromVector(R.drawable.square)))
                    .anchor(1, 1)
                    .position(curr.getBounds().northeast, meters))
                assert(curr != null)
                northeast = curr.getBounds().northeast
                /* Extracts the bottom left vertex */southwest = curr.getBounds().southwest
                /* Calculates top-left vertex */northwest =
                    LatLng(northeast.latitude, southwest.longitude)
                /* Calculates bottom-right vertex */southeast =
                    LatLng(southwest.latitude, northeast.longitude)
                /* Checks if at least one vertex is inside the minimum rectangle */
                val rectVertex: List<LatLng> = rectangle.getPoints()
                if (PolyUtil.containsLocation(northeast, rectVertex, false)
                    || PolyUtil.containsLocation(northwest, rectVertex, false)
                    || PolyUtil.containsLocation(southeast, rectVertex, false)
                    || PolyUtil.containsLocation(southwest, rectVertex, false)
                ) {
                    /* Builds the poly-square on top of square-image */
                    val polygon: Polygon = googleMap.addPolygon(PolygonOptions()
                        .visible(false)
                        .add(northwest)
                        .add(northeast)
                        .add(southeast)
                        .add(southwest))
                    /* Adds the square to the list that will be used
                       for drawing suggested points */polygons.add(polygon)
                    /* Removes actual square-image */curr.remove()
                }
            }
            /* Builds the new square-image on top of the previous one */curr =
                googleMap.addGroundOverlay(GroundOverlayOptions()
                    .visible(false)
                    .image(Objects.requireNonNull(bitmapDescriptorFromVector(R.drawable.square)))
                    .anchor(1, 1)
                    .position(prev.getBounds().southwest, meters))
            prev.remove()
        }
    }


    /**
     * Default configuration of field. Standard suggested points inside the field.
     * Once perimeter is drawn, pass its vertexes to a function that will calculate the minimum
     * rectangle containing perimeter and calculate the grid of cells
     * whose centers are the points that this method draws.
     */
    private fun drawDefaultPolyAndPoints() {
        // array containing points to put inside field
        val pts: ArrayList<Point> = ArrayList()
        // the first one is the perimeter
        pts.add(Point(fromPolygonToString(perimeterPoly), zone++, null))
        // draw the grid of the perimeter
        drawGrid(perimeterPoly.getPoints())
        // iterate over all cells inside minimum rectangle
        for (i in 0 until polygons.size()) {
            val poly: Polygon = polygons[i]
            // save the square of suggested points (center of polygon)
            val coordinates: List<LatLng> = poly.getPoints()
            // extract center of poly
            val center: LatLng = LatLngBounds.builder().include(coordinates[0]).include(
                coordinates[1])
                .include(coordinates[2]).include(coordinates[3])
                .build().getCenter()
            // build point: square containing the point, zoneId, coordinates of point
            val p = Point(fromPolygonToString(poly), zone++,
                center.latitude + " " + center.longitude)
            // set global unique id
            p.setJsonPoint(Gson().toJson(field.getName() + " " + p.getZoneId() + " "
                    + field.getDate()))
            pts.add(p)
            // draw on map the point suggested if it is inside perimeter => si e' vero qui funziona come dovrebbe
            markers.add(googleMap.addMarker(MarkerOptions()
                .visible(PolyUtil.containsLocation(center, perimeterPoly.getPoints(),
                    false))
                .position(fromStringToLatLng(p.getSuggestedPoint())[0])
                .title(p.getZoneId() + "").snippet(makeSnippet(p))
                .icon(bitmapDescriptorFromVector(R.drawable.red_round_shape))))
        }
        configureMapOnceFieldIsDrawn(pts)
    }
}*/