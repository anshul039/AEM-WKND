package com.adobe.aem.guides.wknd.core.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.Binary;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = { Servlet.class }, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET })
@SlingServletPaths("/bin/wknd/sendEmail")
public class EmailServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;

	@Reference
	private MessageGatewayService messageGatewayService;

	private ResourceResolver resourceResolver;
	
	private AssetManager assetManager;
	
	private Session session;

	private static Logger log = LoggerFactory.getLogger(EmailServlet.class);
	
    private static SimpleDateFormat PDF_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("inside EmailServlet");
		resourceResolver = req.getResourceResolver();
		String field1 = req.getParameter("field1");
		String field2 = req.getParameter("field2");
		log.debug("field1 : {}, field2 : {}", field1, field2);
		JSONObject jsonResponse = new JSONObject();
		boolean sent = false;
		try {
			String[] recipients = { "anshul.agarwal@coforge.com" };
			String string = "This is the email attachment. Fields value are : " + field1 + " and " + field2;
			sendEmail("This is an test email",
					"This is the email body. Fields value are : " + field1 + " and " + field2, recipients, string);

			resp.setStatus(200);
			sent = true;
		} catch (EmailException e) {
			log.error("Email Exception : {0}", e);
			resp.setStatus(500);
		} catch(NullPointerException e) {
			e.printStackTrace();
			log.error("NullPointerException Exception : {0}", e);
			resp.setStatus(500);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			log.error("FileNotFoundException Exception : {0}", e);
			resp.setStatus(500);
		} catch (PathNotFoundException e) {
			e.printStackTrace();
			log.error("PathNotFoundException Exception : {0}", e);
			resp.setStatus(500);
		} catch (RepositoryException e) {
			e.printStackTrace();
			log.error("RepositoryException Exception : {0}", e);
			resp.setStatus(500);
		} 
		try {
			jsonResponse.put("result", sent ? "done" : "something went wrong");
		} catch (JSONException e) {
			e.printStackTrace();
			log.error("JSONException Exception : {0}", e);
		}
		resp.setContentType("text/plain");
		resp.getWriter().write(jsonResponse.toString());
	}

	private void sendEmail(String subjectLine, String msgBody, String[] recipients, String attachmentData)
			throws EmailException, IOException, RepositoryException, NullPointerException {
		HtmlEmail email = new HtmlEmail();
		for (String recipient : recipients) {
			email.addTo(recipient, recipient);
		}
		email.setSubject(subjectLine);
		email.setMsg(msgBody);
		Asset asset = createPdf(attachmentData);
		ByteArrayDataSource imageDS = new ByteArrayDataSource(asset.getOriginal().getStream(),"application/pdf");
		email.attach(imageDS,"Sample PDF","Sample Description","inline");
		MessageGateway<Email> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
		if (messageGateway != null) {
			log.debug("sending out email");
			messageGateway.send(email);
		} else {
			log.error("The message gateway could not be retrieved.");
		}
		session.save();
		session.logout();
	}
	
	private Asset createPdf(String attachmentData) throws IOException, UnsupportedRepositoryOperationException, RepositoryException {
		PDDocument document = new PDDocument();

		// Create a new page
		PDPage page = new PDPage();
		document.addPage(page);

		// Create a content stream for the page
		PDPageContentStream contentStream = new PDPageContentStream(document, page);

		// Write some text to the content stream
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
		contentStream.newLineAtOffset(100, 700);
		contentStream.showText(attachmentData);
		contentStream.endText();
		contentStream.close();

		// Save the PDF document to a ByteArrayOutputStream
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		document.save(outputStream);
		document.close();
		
		// Adapt the ResourceResolver to a Session
		session = resourceResolver.adaptTo(Session.class);

		// Convert the ByteArrayOutputStream to a Binary
		final ValueFactory valueFactory = session.getValueFactory();
		Binary binary = valueFactory.createBinary(new ByteArrayInputStream(outputStream.toByteArray()));

		// Specify the DAM path where you want to store the PDF
        String assetPath = "/content/dam/pdfs/retail/test-" + PDF_DATE_FORMAT.format(new Date()) + ".pdf";

		// Create or replace the asset in the DAM
        assetManager = resourceResolver.adaptTo(AssetManager.class);
		return assetManager.createOrReplaceAsset(assetPath, binary, "application/pdf", true);
	}
	
}
