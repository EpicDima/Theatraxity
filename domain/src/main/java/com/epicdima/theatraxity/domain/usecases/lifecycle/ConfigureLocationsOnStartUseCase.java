package com.epicdima.theatraxity.domain.usecases.lifecycle;

import com.epicdima.theatraxity.domain.models.business.Ticket;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * @author EpicDima
 */
public final class ConfigureLocationsOnStartUseCase {

    public void execute(InputStream inputStream) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            NodeList locationNodes = document.getElementsByTagName("Location");
            for (int i = 0; i < locationNodes.getLength(); i++) {
                NamedNodeMap attributes = locationNodes.item(i).getAttributes();
                Ticket.Location location = Ticket.Location.valueOf(attributes.getNamedItem("name").getNodeValue());
                Node attr = attributes.getNamedItem("ticketCost");
                if (attr != null) {
                    location.setTicketCost(new BigDecimal(attr.getNodeValue()));
                }
                attr = attributes.getNamedItem("rows");
                if (attr != null) {
                    location.setRows(Integer.parseInt(attr.getNodeValue()));
                }
                attr = attributes.getNamedItem("seats");
                if (attr != null) {
                    location.setSeats(Integer.parseInt(attr.getNodeValue()));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
