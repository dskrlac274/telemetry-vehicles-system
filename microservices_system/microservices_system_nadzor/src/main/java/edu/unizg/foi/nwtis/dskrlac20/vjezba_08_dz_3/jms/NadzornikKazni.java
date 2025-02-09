package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jms;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Poruka;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.websocket.WebSocketPosluzitelj;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

/**
 * Message-driven bean za primanje poruka iz reda nwtisQ i slanje preko WebSocket-a.
 */
@MessageDriven(mappedName = "jms/nwtisQ", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")})
public class NadzornikKazni implements MessageListener {
  public NadzornikKazni() {
  }

  /**
   * Metoda koja se poziva kada stigne poruka na red nwtisQ.
   *
   * @param message primljena JMS poruka
   */
  public void onMessage(Message message) {
    if (message instanceof ObjectMessage) {
      try {
        ObjectMessage msg = (ObjectMessage) message;
        Poruka poruka = (Poruka) msg.getObject();

        try (Jsonb jsonb = JsonbBuilder.create()) {
          String jsonPoruka = jsonb.toJson(poruka);
          WebSocketPosluzitelj.send(jsonPoruka);
        }
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
      }
    }
  }
}
