package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.jms;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Poruka;
import jakarta.ejb.Stateless;
import jakarta.annotation.Resource;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.Session;

/**
 * Stateless session bean za slanje poruka o novim kaznama na JMS red.
 * Koristi se za slanje poruke o novoj kazni na određeni JMS red.
 */
@Stateless
public class PrijenosnikKazni {
  @Resource(mappedName = "jms/nwtisCF")
  private ConnectionFactory tvornicaVeza;
  @Resource(mappedName = "jms/nwtisQ")
  private Queue red;

  /**
   * Metoda za slanje poruke o novoj kazni na JMS red.
   * Kreira JMS vezu, sesiju i poruku te šalje poruku o novoj kazni na odabrani JMS red.
   *
   * @param poruka Podaci o novoj kazni u obliku {@link Poruka} objekta
   * @return true ako je poruka uspješno poslana, inače false
   */
  public boolean novaKazna(Poruka poruka) {
    boolean status = true;

    try {
      Connection veza = tvornicaVeza.createConnection();
      Session sesija = veza.createSession(false, Session.AUTO_ACKNOWLEDGE);
      MessageProducer porukaProducer = sesija.createProducer(red);
      ObjectMessage porukaPodaci = sesija.createObjectMessage();

      porukaPodaci.setObject(poruka);
      porukaProducer.send(porukaPodaci);
      porukaProducer.close();
      veza.close();
    } catch (JMSException ex) {
      status = false;
    }
    return status;
  }
}
