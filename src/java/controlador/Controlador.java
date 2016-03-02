/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import gestion.GestorKeep;
import gestion.GestorUsuario;
import hibernate.Keep;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author izv
 */
@WebServlet(name = "Controlador", urlPatterns = {"/go"})
public class Controlador extends HttpServlet {

    enum Camino {
        forward, redirect, print;
    }

    class Destino {

        public Camino camino;
        public String url;
        public String texto;

        public Destino() {
        }

        public Destino(Camino camino, String url, String texto) {
            this.camino = camino;
            this.url = url;
            this.texto = texto;
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        String tabla = request.getParameter("tabla");//persona, idioma, etc.
        String op = request.getParameter("op");//login, create, read, update, delete
        String accion = request.getParameter("accion");//view, do
        String origen = request.getParameter("origen");//android, web
        Destino destino = handle(request, response, tabla, op, accion, origen);
        if (destino == null) {
            destino = new Destino(Camino.forward, "/WEB-INF/index.jsp", "");
        }
        if (destino.camino == Camino.forward) {
            request.getServletContext().
                    getRequestDispatcher(destino.url).forward(request, response);
        } else if (destino.camino == Camino.redirect) {
            response.sendRedirect(destino.url);
        } else {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println(destino.texto);
            }
        }
    }

    private Destino handle(HttpServletRequest request, HttpServletResponse response,
            String tabla, String op, String accion, String origen) throws JSONException {
        if (origen == null) {
            origen = "";
        }
        if (tabla == null || op == null || accion == null) {
            tabla = "usuario";
            op = "read";
            accion = "view";
        }
        switch (tabla) {
            case "usuario":
                return handleUsuario(request, response, op, accion, origen);
            case "keep":
                return handleKeep(request, response, op, accion, origen);
            default:
        }
        return null;
    }

    private Destino handleUsuario(HttpServletRequest request, HttpServletResponse response,
            String op, String accion, String origen) throws JSONException {

        switch (op) {
            case "login":
                if (origen.equals("android")) {
                    JSONObject obj = GestorUsuario.getLogin(request.getParameter("login"),
                            request.getParameter("pass"));
                    return new Destino(Camino.print, "", obj.toString());
                }
                if (origen.equals("web")) {
                    JSONObject obj = GestorUsuario.getLogin(request.getParameter("login"),
                            request.getParameter("pass"));
                    if (obj.getBoolean("r")) {
                        List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                        request.setAttribute("listado", keeps);
                        request.setAttribute("login", GestorUsuario.getUserbyName(request.getParameter("login")));
                        return new Destino(Camino.forward, "/WEB-INF/viewkeeps.jsp", keeps.toString());
                    } else {
                        return new Destino(Camino.print, "index.html", obj.toString());
                    }
                }

        }
        return null;
    }

    private Destino handleKeep(HttpServletRequest request, HttpServletResponse response,
            String op, String accion, String origen) throws JSONException {

        switch (op) {
            case "actualiza":
                System.out.println("ENTRA");
                Keep keep = new Keep(null, -1,
                        request.getParameter("contenido"), null, "estable");
                JSONObject obje = GestorKeep.removeKeep(keep, request.getParameter("login"));
                return new Destino(Camino.print, "", obje.toString());
            case "create":
                if (origen.equals("android")) {
                    Keep k = new Keep(null, Integer.parseInt(request.getParameter("idAndroid")),
                            request.getParameter("contenido"), null, "estable");
                    JSONObject obj = GestorKeep.addKeep(k,
                            request.getParameter("login"));
                    // GestorKeep.addKeep(k);
                    return new Destino(Camino.print, "", obj.toString());
                }
                if (origen.equals("web")) {
                    Keep k = new Keep(null, -1, request.getParameter("contenido"), null, "estable");
                    JSONObject obj = GestorKeep.addKeep(k, request.getParameter("login"));
                    List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                    request.setAttribute("listado", keeps);
                    return new Destino(Camino.forward, "/WEB-INF/viewkeeps.jsp", keeps.toString());

                }
            case "read":
                if (origen.equals("android")) {
                    JSONObject obj = GestorKeep.getKeeps(request.getParameter("login"));
                    return new Destino(Camino.print, "", obj.toString());
                }
            case "delete":
                if (origen.equals("android")) {
                    Keep k = new Keep(null, Integer.parseInt(request.getParameter("idAndroid")),
                            request.getParameter("contenido"), null, "estable");
                    JSONObject obj = GestorKeep.removeKeep(k, request.getParameter("login"));
                    return new Destino(Camino.print, "", obj.toString());
                }
                if (origen.equals("web")) {
                    int id=Integer.parseInt(request.getParameter("id"));
                    GestorKeep.removeKeepWeb(id);
                    List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                    request.setAttribute("listado", keeps);
                    return new Destino(Camino.forward, "/WEB-INF/viewkeeps.jsp", keeps.toString());
                }
            case "update":
                if (origen.equals("web")) {
                    int id=Integer.parseInt(request.getParameter("id"));
                    int idAndroid=Integer.parseInt(request.getParameter("idAndroid"));
                    
                    Keep k = new Keep(id, idAndroid,
                            request.getParameter("contenido"), "estable");
                    
                    GestorKeep.updateKeep(k);
                    List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                    request.setAttribute("listado", keeps);
                    return new Destino(Camino.forward, "/WEB-INF/viewkeeps.jsp", keeps.toString());
                }

        }

        return null;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    /*
        1 tabla :  keep, usuario
        2 op: login, create, read, update, delete
        3 accion: view, do
        4 origen: android, web
    
    
    
    
     */
}
