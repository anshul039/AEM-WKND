/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.aem.guides.wknd.core.servlets;

import static com.day.crx.JcrConstants.JCR_DATA;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
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

import com.day.cq.dam.api.AssetManager;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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

	private static Logger log = LoggerFactory.getLogger(EmailServlet.class);

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		resourceResolver = req.getResourceResolver();
		String field1 = req.getParameter("field1");
		String field2 = req.getParameter("field2");
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
		} catch (MessagingException e) {
			e.printStackTrace();
			log.error("MessagingException Exception : {0}", e);
			resp.setStatus(500);
		} catch (PathNotFoundException e) {
			e.printStackTrace();
			log.error("PathNotFoundException Exception : {0}", e);
			resp.setStatus(500);
		} catch (RepositoryException e) {
			e.printStackTrace();
			log.error("RepositoryException Exception : {0}", e);
			resp.setStatus(500);
		} catch (DocumentException e) {
			e.printStackTrace();
			log.error("DocumentException Exception : {0}", e);
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
			throws EmailException, IOException, MessagingException, PathNotFoundException, RepositoryException, DocumentException {
		HtmlEmail email = new HtmlEmail();
		for (String recipient : recipients) {
			email.addTo(recipient, recipient);
		}
		email.setSubject(subjectLine);
		email.setMsg(msgBody);

		String path = "/content/dam/pdfs/retail/test.pdf";

		Resource rs = resourceResolver.getResource(path);
		if (null == rs) {
			Resource res1 = resourceResolver.getResource("/content/dam/pdfs/retail");
			if (null == res1) {
				Resource res2 = resourceResolver.getResource("/content/dam/pdfs");
				if (null == res2) {
					Resource res3 = resourceResolver.getResource("/content/dam");
					resourceResolver.create(res3, "pdfs", null);
					resourceResolver.commit();
				}
				resourceResolver.create(res2, "retail", null);
				resourceResolver.commit();
			}
		}
		createPdf(attachmentData, path);
		Session session = resourceResolver.adaptTo(Session.class);
		AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
		InputStream stream = new ByteArrayInputStream(attachmentData.getBytes(StandardCharsets.UTF_8));
		final ValueFactory valueFactory = session.getValueFactory();
		final Binary binary = valueFactory.createBinary(stream);

		assetManager.createOrReplaceAsset(path, binary, "application/pdf", true);
		session.save();
		
//		Node resNode = session.getNode(path + "/jcr:content/renditions/original/jcr:content");
//		if(null == resNode) {
//			log.error("Node does not exist");
//		}
//		resNode.setProperty("jcr:mimeType", "application/pdf");
//		resNode.setProperty("jcr:data", attachmentData);
//		session.save();
//		EmailAttachment attachment = new EmailAttachment();
//		attachment.setPath(path);
//		attachment.setDisposition(EmailAttachment.ATTACHMENT);
//		attachment.setDescription("Sample Pdf data");
//		attachment.setName("sample pdf");
//		email.attach(attachment);
		
//		Resource imageNodeRes = resourceResolver.getResource(path);
//		Node imageNode=imageNodeRes.adaptTo(Node.class);
//		Node contentNode = imageNode.getNode("jcr:content");
//		Binary imageBinary = contentNode.getProperty("jcr:data").getBinary();
//		InputStream imageStream = imageBinary.getStream();
		
//		Resource res = resourceResolver.getResource(path + "/jcr:content/renditions/original");
		ByteArrayDataSource imageDS = new ByteArrayDataSource(stream,"application/pdf");
//		ByteArrayDataSource imageDS = getByteArrayDataSource(res);
		email.attach(imageDS,"Sample PDF","Sample Description");
		
//		InputStream stream = new ByteArrayInputStream(attachmentData.getBytes(Charset.forName("UTF-8")));
//		MimeMultipart multipart = new MimeMultipart();
//		BodyPart bodyPart = new MimeBodyPart(stream);
//		multipart.addBodyPart(bodyPart);

//		final MailTemplate mailTemplate = MailTemplate.create(path, session);
//		Map<String, String> params = new HashMap();
//		params.put("importEntity", "Entity1");
//		HtmlEmail email2 = mailTemplate.getEmail(StrLookup.mapLookup(params), HtmlEmail.class);

		MessageGateway<Email> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
		if (messageGateway != null) {
			log.debug("sending out email");
			messageGateway.send(email);
		} else {
			log.error("The message gateway could not be retrieved.");
		}
	}
	
	private PdfWriter createPdf(String attachmentData, String path) throws FileNotFoundException, DocumentException {
		Document document = new Document();
		PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path));
		document.open();
        document.add(new Paragraph(attachmentData));
        document.close();
        pdfWriter.close();
//        document.
        return pdfWriter;
	}
}
