package foodtrucks;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ProxyQuery is Servlet that accepts requests with coordinate boundaries, and proxies it to the 
 *  SFData Food Truck API.  The data displayed is the same json data returned by this API
 */
public class ProxyQuery extends HttpServlet {
   
    private static final String HOST_PATH = "http://data.sfgov.org/resource/rqzj-sfat.json?Status=APPROVED";
    
    /** 
     * Proxy a request to SFData Food Truck API.  Read parameters include:
     *  - north: the upper latitude boundary
     *  - south: the lower latitude boundary
     *  - east: the upper longitude boundary
     *  - west: the lower longitude boundary
     * 
     * @param request servlet request
     * @param response servlet response
     * 
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            // read the bounds
            String north = request.getParameter("north");
            String south = request.getParameter("south");
            String east = request.getParameter("east");
            String west = request.getParameter("west");
            
            // build the full query
            StringBuilder queryString = new StringBuilder(HOST_PATH);
            
            Double northDouble = null;
            Double southDouble = null;
            Double eastDouble = null;
            Double westDouble = null;
            
            // validate inputs are numeric at least
            try {
                if (null != north && !north.isEmpty()) {
                    northDouble = Double.valueOf(north);
                }
                
                if (null != south && !south.isEmpty()) {
                    southDouble = Double.valueOf(south);
                }
                
                if (null != east && !east.isEmpty()) {
                    eastDouble = Double.valueOf(east);
                }
                
                if (null != west && !west.isEmpty()) {
                    westDouble = Double.valueOf(west);
                }
                
            } catch (NumberFormatException nfe) {
                // ignore the values in the query
            }
            
            // if all inputs are valid, include them as bounds in the query
            if (null != northDouble && null != southDouble && null != eastDouble && null != westDouble &&
                    northDouble.doubleValue() > southDouble.doubleValue() && 
                    // when taking values near San Francisco, east is greater than west
                    eastDouble.doubleValue() > westDouble.doubleValue()) {
                queryString.append("&$where=latitude>");
                queryString.append(south);
                queryString.append("%20AND%20latitude<");
                queryString.append(north);
                queryString.append("%20AND%20longitude<");
                queryString.append(east);
                queryString.append("%20AND%20longitude>");
                queryString.append(west);
            }
            
            // query the data
            URL query = new URL(queryString.toString());
            InputStream input = query.openStream();
            
            // add the data to the response
            IOUtils.copy(input, out, "UTF-8");
            
        /*} catch (ParseException ex) {
            Logger.getLogger(ProxyQuery.class.getName()).log(Level.SEVERE, null, ex);*/
        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Proxy Food Trucks Query";
    }// </editor-fold>

}
