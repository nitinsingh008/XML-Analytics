//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.15 at 11:31:31 PM IST 
//


package com.concept.crew.info.jaxb;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PutSchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PutSchedule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="putScheduleSource" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="30"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="putStartDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="putEndDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="putValidity" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="putFee" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="putFrequencyCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="putPrice" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="isSpecialPut" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="putMaxNoticeDays" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="putMinNoticeDays" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="optionStyle" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="200"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="optionStyleSubType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="200"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="putNoticeDaysConvention" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="putMethodType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="200"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="putMethodSubType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="200"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="putPriceLinkage" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="50"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="isPriceDifferentFromPar" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="makewholeBenchmark" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="makewholeSpread" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PutSchedule", propOrder = {
    "putScheduleSource",
    "putStartDate",
    "putEndDate",
    "putValidity",
    "putFee",
    "putFrequencyCode",
    "putPrice",
    "isSpecialPut",
    "putMaxNoticeDays",
    "putMinNoticeDays",
    "optionStyle",
    "optionStyleSubType",
    "putNoticeDaysConvention",
    "putMethodType",
    "putMethodSubType",
    "putPriceLinkage",
    "isPriceDifferentFromPar",
    "makewholeBenchmark",
    "makewholeSpread"
})
public class PutSchedule {

    protected String putScheduleSource;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar putStartDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar putEndDate;
    protected Long putValidity;
    protected Double putFee;
    protected String putFrequencyCode;
    protected BigDecimal putPrice;
    protected Boolean isSpecialPut;
    protected Long putMaxNoticeDays;
    protected Long putMinNoticeDays;
    protected String optionStyle;
    protected String optionStyleSubType;
    protected String putNoticeDaysConvention;
    protected String putMethodType;
    protected String putMethodSubType;
    protected String putPriceLinkage;
    protected Boolean isPriceDifferentFromPar;
    protected String makewholeBenchmark;
    protected Double makewholeSpread;

    /**
     * Gets the value of the putScheduleSource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPutScheduleSource() {
        return putScheduleSource;
    }

    /**
     * Sets the value of the putScheduleSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPutScheduleSource(String value) {
        this.putScheduleSource = value;
    }

    /**
     * Gets the value of the putStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPutStartDate() {
        return putStartDate;
    }

    /**
     * Sets the value of the putStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPutStartDate(XMLGregorianCalendar value) {
        this.putStartDate = value;
    }

    /**
     * Gets the value of the putEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPutEndDate() {
        return putEndDate;
    }

    /**
     * Sets the value of the putEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPutEndDate(XMLGregorianCalendar value) {
        this.putEndDate = value;
    }

    /**
     * Gets the value of the putValidity property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPutValidity() {
        return putValidity;
    }

    /**
     * Sets the value of the putValidity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPutValidity(Long value) {
        this.putValidity = value;
    }

    /**
     * Gets the value of the putFee property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPutFee() {
        return putFee;
    }

    /**
     * Sets the value of the putFee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPutFee(Double value) {
        this.putFee = value;
    }

    /**
     * Gets the value of the putFrequencyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPutFrequencyCode() {
        return putFrequencyCode;
    }

    /**
     * Sets the value of the putFrequencyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPutFrequencyCode(String value) {
        this.putFrequencyCode = value;
    }

    /**
     * Gets the value of the putPrice property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPutPrice() {
        return putPrice;
    }

    /**
     * Sets the value of the putPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPutPrice(BigDecimal value) {
        this.putPrice = value;
    }

    /**
     * Gets the value of the isSpecialPut property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSpecialPut() {
        return isSpecialPut;
    }

    /**
     * Sets the value of the isSpecialPut property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSpecialPut(Boolean value) {
        this.isSpecialPut = value;
    }

    /**
     * Gets the value of the putMaxNoticeDays property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPutMaxNoticeDays() {
        return putMaxNoticeDays;
    }

    /**
     * Sets the value of the putMaxNoticeDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPutMaxNoticeDays(Long value) {
        this.putMaxNoticeDays = value;
    }

    /**
     * Gets the value of the putMinNoticeDays property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPutMinNoticeDays() {
        return putMinNoticeDays;
    }

    /**
     * Sets the value of the putMinNoticeDays property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPutMinNoticeDays(Long value) {
        this.putMinNoticeDays = value;
    }

    /**
     * Gets the value of the optionStyle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionStyle() {
        return optionStyle;
    }

    /**
     * Sets the value of the optionStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionStyle(String value) {
        this.optionStyle = value;
    }

    /**
     * Gets the value of the optionStyleSubType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptionStyleSubType() {
        return optionStyleSubType;
    }

    /**
     * Sets the value of the optionStyleSubType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptionStyleSubType(String value) {
        this.optionStyleSubType = value;
    }

    /**
     * Gets the value of the putNoticeDaysConvention property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPutNoticeDaysConvention() {
        return putNoticeDaysConvention;
    }

    /**
     * Sets the value of the putNoticeDaysConvention property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPutNoticeDaysConvention(String value) {
        this.putNoticeDaysConvention = value;
    }

    /**
     * Gets the value of the putMethodType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPutMethodType() {
        return putMethodType;
    }

    /**
     * Sets the value of the putMethodType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPutMethodType(String value) {
        this.putMethodType = value;
    }

    /**
     * Gets the value of the putMethodSubType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPutMethodSubType() {
        return putMethodSubType;
    }

    /**
     * Sets the value of the putMethodSubType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPutMethodSubType(String value) {
        this.putMethodSubType = value;
    }

    /**
     * Gets the value of the putPriceLinkage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPutPriceLinkage() {
        return putPriceLinkage;
    }

    /**
     * Sets the value of the putPriceLinkage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPutPriceLinkage(String value) {
        this.putPriceLinkage = value;
    }

    /**
     * Gets the value of the isPriceDifferentFromPar property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsPriceDifferentFromPar() {
        return isPriceDifferentFromPar;
    }

    /**
     * Sets the value of the isPriceDifferentFromPar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsPriceDifferentFromPar(Boolean value) {
        this.isPriceDifferentFromPar = value;
    }

    /**
     * Gets the value of the makewholeBenchmark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMakewholeBenchmark() {
        return makewholeBenchmark;
    }

    /**
     * Sets the value of the makewholeBenchmark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMakewholeBenchmark(String value) {
        this.makewholeBenchmark = value;
    }

    /**
     * Gets the value of the makewholeSpread property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMakewholeSpread() {
        return makewholeSpread;
    }

    /**
     * Sets the value of the makewholeSpread property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMakewholeSpread(Double value) {
        this.makewholeSpread = value;
    }

}
