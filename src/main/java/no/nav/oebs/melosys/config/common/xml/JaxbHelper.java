package no.nav.oebs.melosys.config.common.xml;

import java.io.StringReader;
import java.io.StringWriter;

import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 * Hjelpeklasse for JAXB-operasjoner som konverterer mellom JAXB-objekter og XML.
 */
public class JaxbHelper {

	private JAXBContext jaxbContext;

	/**
	 * Oppretter en JaxbHelper for spesifiserte JAXB-annoterte klasser.
	 * <p>
	 * Kaster {@link DataBindingException} ved feil.
	 * 
	 * @param classes
	 *            JAXB-annoterte klasser som skal håndteres.
	 */
	public JaxbHelper(Class<?>... classes) {
		try {
			// JAXBContext-objekter er thread-safe (men ikke Marshaller og Unmarshaller).
			jaxbContext = JAXBContext.newInstance(classes);
		} catch (JAXBException e) {
			throw new DataBindingException(e);
		}
	}

	/**
	 * Konverterer et JAXB-objekt til XML.
	 * <p>
	 * Kaster {@link DataBindingException} ved feil.
	 * 
	 * @param <T>
	 *            typen til JAXB-objektet.
	 * @param jaxbObject
	 *            JAXB-objektet som skal konverteres.
	 * @return XML på stringformat.
	 */
	public <T> String marshalObject(T jaxbObject) {
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();

			StringWriter writer = new StringWriter();
			marshaller.marshal(jaxbObject, writer);

			return writer.toString();
		} catch (JAXBException e) {
			throw new DataBindingException(e);
		}
	}

	/**
	 * Konverterer en XML på stringformat til et JAXB-objekt.
	 * <p>
	 * Kaster {@link DataBindingException} ved feil.
	 * 
	 * @param <T>
	 *            typen til JAXB-objektet.
	 * @param xml
	 *            XML som skal konverteres.
	 * @param type
	 *            klassen til JAXB-objektet.
	 * @return JAXB-objektet.
	 */
	public <T> T unmarshalXml(String xml, Class<T> type) {
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StreamSource source = new StreamSource(new StringReader(xml));

			return unmarshaller.unmarshal(source, type).getValue();
		} catch (JAXBException e) {
			throw new DataBindingException(e);
		}
	}
}
