import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    String name;
    QuadTree overview = new QuadTree(ROOT_ULLON, ROOT_ULLAT, ROOT_LRLON, ROOT_LRLAT,   "");

    //Y as latitude and X as longitude//



    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        name = imgRoot;
        overview.construct(overview.root);
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        String[][] rendergrid;
        double lrlat = params.get("lrlat");
        double lrlon  = params.get("lrlon");
        double ullat = params.get("ullat");
        double ullon = params.get("ullon");
        double w = params.get("w");
        double deltalon = lrlon - ullon;

        //longitudinal distance per pixel (LonDPP)
        double lonDPP = deltalon / w; //units of longitude per pixel
        Map<String, Object> results = new HashMap<>();
        TreeSet list = new TreeSet();
        QuadTree.Node query = new QuadTree.Node(ullon, ullat, lrlon, lrlat, "QueryBox");
        TreeSet<QuadTree.Node> overlap = rightimage(query, overview.root, lonDPP, list);
        QuadTree.Node upperleft = overlap.first();
        QuadTree.Node lowerright = overlap.last();
        double rasterUlLon = upperleft.upperleftx;
        double rasterUlLat = upperleft.upperlefty;
        double rasterLrLon = lowerright.lowerrightx;
        double rasterLrLat = lowerright.lowerrighty;
        Double numw = Math.ceil((lowerright.lowerrightx
                - upperleft.upperleftx) / (upperleft.LonDPP * 256));
        int xSize = numw.intValue();
        int ySize = overlap.size() / xSize;
        rendergrid = new String[ySize][xSize];
        Iterator iter = overlap.iterator();
        QuadTree.Node[] overlaparray = new QuadTree.Node[overlap.size()];
        int index = 0;
        while (iter.hasNext()) {
            overlaparray[index] = (QuadTree.Node) iter.next();
            index += 1;
        }
        for (int i = 0; i < ySize; i += 1) {
            for (int j = 0; j < xSize; j += 1) {
                rendergrid[i][j] =  "img/" + overlaparray[i * xSize + j].name() + ".png";
            }
        }
        //boolean query_success = upperleft != null && lowerright != null;
        int depth = upperleft.name().length();
        results.put("render_grid", rendergrid);
        results.put("raster_ul_lon", rasterUlLon);
        results.put("raster_ul_lat", rasterUlLat);
        results.put("raster_lr_lon", rasterLrLon);
        results.put("raster_lr_lat", rasterLrLat);
        results.put("depth", depth);
        results.put("query_success", true);
        return results;
    }


    /**if (RectA.Left < RectB.Right && RectA.Right > RectB.Left &&
     RectA.Top < RectB.Bottom && RectA.Bottom > RectB.Top ) */


    public TreeSet<QuadTree.Node> rightimage(QuadTree.Node query, QuadTree.Node node,
                                             double lonDPP, TreeSet<QuadTree.Node> list) {
        if (QuadTree.Node.intersect(query, node)) {
            if (node.LonDPP <= lonDPP || node.name().length() == 7) {
                list.add(node);
            } else {
                if (QuadTree.Node.intersect(query, node.child1)) {
                    rightimage(query, node.child1, lonDPP, list);
                }
                if (QuadTree.Node.intersect(query, node.child2)) {
                    rightimage(query, node.child2, lonDPP, list);
                }
                if (QuadTree.Node.intersect(query, node.child3)) {
                    rightimage(query, node.child3, lonDPP, list);
                }
                if (QuadTree.Node.intersect(query, node.child4)) {
                    rightimage(query, node.child4, lonDPP, list);
                }
            }
            return list;
        } else {
            return list;
        }
    }


    public static void main(String[] args) {
        Rasterer example = new Rasterer("");
        Map<String, Double> params = new HashMap<>();
        params.put("lrlat", 37.83495035769344);
        params.put("lrlon", -122.2119140625);
        params.put("ullat", 37.88746545843562);
        params.put("ullon", -122.2591326176749);
        params.put("w", 929.0);
        params.put("h", 944.0);
        example.getMapRaster(params);
    }
}

