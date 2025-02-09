package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.websocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Kazna;
import edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.podaci.Poruka;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * WebSocket poslužitelj koji omogućava komunikaciju preko WebSocket protokola
 * za razmjenu podataka o kaznama.
 */
@ServerEndpoint("/kazne")
public class WebSocketPosluzitelj {

  static Queue<Session> queue = new ConcurrentLinkedQueue<>();

  /**
   * Metoda za slanje JSON poruke svim povezanim WebSocket sesijama.
   * Ova metoda je statička kako bi se mogla pozvati iz drugih dijelova aplikacije.
   *
   * @param porukaJson JSON poruka koja se šalje
   */
  public static void send(String porukaJson) {
    try (Jsonb jsonb = JsonbBuilder.create()) {
      Poruka poruka = jsonb.fromJson(porukaJson, Poruka.class);
      Kazna kazna = poruka.getKazna();

      SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
      String vrijemeNastanka = dateFormat.format(kazna.getVrijemeKraj());
      String vrijemePrijema = dateFormat.format(poruka.getVrijeme());
      String vrijemeObjave = dateFormat.format(new Date());

      String formattedMessage = String.format(
          "%s Lokacija: %s, %s Nastala: %s Prijem: %s Objava: %s",
          poruka.getSadrzaj(), kazna.getGpsSirina(), kazna.getGpsDuzina(),
          vrijemeNastanka, vrijemePrijema, vrijemeObjave
      );

      for (Session session : queue) {
        if (session.isOpen()) {
          session.getBasicRemote().sendText(formattedMessage);
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }

  /**
   * Metoda koja se poziva kada se otvori nova WebSocket veza.
   * Dodaje novu sesiju u red aktivnih sesija.
   *
   * @param session Sesija koja je otvorena
   * @param conf Konfiguracija endpointa (nije korisćeno u ovom slučaju)
   */
  @OnOpen
  public void openConnection(Session session, EndpointConfig conf) {
    queue.add(session);
    System.out.println("Otvorena veza.");
  }

  /**
   * Metoda koja se poziva kada se zatvori WebSocket veza.
   * Uklanja zatvorenu sesiju iz reda aktivnih sesija.
   *
   * @param session Sesija koja se zatvorila
   * @param reason Razlog zatvaranja veze
   */
  @OnClose
  public void closedConnection(Session session, CloseReason reason) {
    queue.remove(session);
    System.out.println("Zatvorena veza.");
  }

  /**
   * Metoda koja se poziva kada se primi poruka od klijenta preko WebSocket-a.
   * Ispisuje primljenu poruku i proslijeđuje je metodi 'send' za daljnju obradu.
   *
   * @param session Sesija preko koje je primljena poruka
   * @param poruka Tekstualna poruka primljena od klijenta
   */
  @OnMessage
  public void Message(Session session, String poruka) {
    System.out.println("Primljena poruka: " + poruka);
    WebSocketPosluzitelj.send(poruka);
  }

  /**
   * Metoda koja se poziva kada dođe do greške u WebSocket sesiji.
   * Uklanja sesiju iz reda aktivnih sesija i ispisuje poruku o grešci.
   *
   * @param session Sesija u kojoj je nastupila greška
   * @param t Throwable objekt koji predstavlja grešku
   */
  @OnError
  public void error(Session session, Throwable t) {
    queue.remove(session);
    System.out.println("Zatvorena veza zbog pogreške.");
  }
}
