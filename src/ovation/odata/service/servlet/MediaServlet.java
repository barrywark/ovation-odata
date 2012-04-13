package ovation.odata.service.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ovation.IEntityBase;
import ovation.Resource;
import ovation.odata.model.ResourceModel;

public class MediaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// determine the key of the requested media (Resource)
		String uri = req.getParameter("uri");
		IEntityBase entity = ResourceModel.getByURI(uri);
		if (entity == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unable to find " + uri);
			return;
		}
		if (entity instanceof Resource == false) {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, entity + " is of wrong type");
			return;
		}
		Resource res = (Resource)entity;
		
		
		// load the requested media
		
		// set response content type
		
		// stream media back to client
	}
}