package edu.unizg.foi.nwtis.dskrlac20.vjezba_08_dz_3.slusaci;


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Slušač aplikacije koji se koristi za učitavanje konfiguracijskih podataka
 * prilikom inicijalizacije konteksta Servlet aplikacije.
 */
public final class ContextListener implements ServletContextListener {
  private ServletContext context = null;

  /**
   * Metoda koja se poziva prilikom inicijalizacije Servlet konteksta.
   * Učitava konfiguracijske podatke iz datoteke 'konfiguracija.txt' i postavlja ih
   * kao atribute konteksta.
   *
   * @param event Događaj inicijalizacije konteksta
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    context = event.getServletContext();
    ucitajKonfiguraciju();
  }

  /**
   * Privatna metoda koja učitava konfiguracijske podatke iz datoteke 'konfiguracija.txt'
   * i postavlja ih kao atribute u Servlet kontekst.
   */
  private void ucitajKonfiguraciju() {
    java.util.Properties konfig = new java.util.Properties();
    String putanja = context.getRealPath("/WEB-INF") + java.io.File.separator;

    try {
      try (java.io.FileInputStream fis =
          new java.io.FileInputStream(putanja + "konfiguracija.txt")) {
        konfig.load(fis);
      }
      for (Enumeration<?> e = konfig.propertyNames(); e.hasMoreElements();) {
        String key = (String) e.nextElement();
        context.setAttribute(key, konfig.getProperty(key));
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
