package com.epicdima.theatraxity.domain.usecases.lifecycle;

import com.epicdima.theatraxity.domain.models.business.Ticket;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

/**
 * @author EpicDima
 */
public final class ConfigureLocationsOnStopUseCase {

    public void execute(OutputStream outputStream) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("config");
            document.appendChild(root);
            for (Ticket.Location location : Ticket.Location.values()) {
                if (location.getNumberOfAllSeats() > 0) {
                    Element locationElement = document.createElement("Location");
                    locationElement.setAttribute("name", location.name());
                    locationElement.setAttribute("ticketCost", String.valueOf(location.getTicketCost()));
                    locationElement.setAttribute("rows", String.valueOf(location.getRows()));
                    locationElement.setAttribute("seats", String.valueOf(location.getSeats()));
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
