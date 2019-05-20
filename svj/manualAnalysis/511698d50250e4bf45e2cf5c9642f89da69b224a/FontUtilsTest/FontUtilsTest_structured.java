/*
 * Created on 13/nov/2012
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.util;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.sejda.impl.sambox.util.FontUtils.canDisplay;
import static org.sejda.impl.sambox.util.FontUtils.fontOrFallback;
import static org.sejda.impl.sambox.util.FontUtils.getStandardType1Font;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.junit.Test;
import org.sejda.core.support.io.IOUtils;
import org.sejda.impl.sambox.component.DefaultPdfSourceOpener;
import org.sejda.impl.sambox.component.PDDocumentHandler;
import org.sejda.impl.sambox.component.PageTextWriter;
import org.sejda.impl.sambox.component.PdfTextExtractorByArea;
import org.sejda.impl.sambox.component.TextWithFont;
import org.sejda.io.SeekableSources;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.pdf.StandardType1Font;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.input.PDFParser;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.PDResources;
import org.sejda.sambox.pdmodel.font.FontMappers;
import org.sejda.sambox.pdmodel.font.FontMapping;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.font.PDType1Font;
import org.sejda.sambox.pdmodel.graphics.form.PDFormXObject;

/**
 * @author Andrea Vacondio
 */
public class FontUtilsTest {

    private static PDFont HELVETICA = FontUtils.HELVETICA;

    @Test
    public void testGetStandardType1Fontg() {
        assertEquals(PDType1Font.COURIER, getStandardType1Font(StandardType1Font.CURIER));
        assertEquals(PDType1Font.COURIER_BOLD, getStandardType1Font(StandardType1Font.CURIER_BOLD));
        assertEquals(PDType1Font.COURIER_BOLD_OBLIQUE, getStandardType1Font(StandardType1Font.CURIER_BOLD_OBLIQUE));
        assertEquals(PDType1Font.COURIER_OBLIQUE, getStandardType1Font(StandardType1Font.CURIER_OBLIQUE));
        assertEquals(PDType1Font.HELVETICA, getStandardType1Font(StandardType1Font.HELVETICA));
        assertEquals(PDType1Font.HELVETICA_BOLD, getStandardType1Font(StandardType1Font.HELVETICA_BOLD));
        assertEquals(PDType1Font.HELVETICA_BOLD_OBLIQUE, getStandardType1Font(StandardType1Font.HELVETICA_BOLD_OBLIQUE));
        assertEquals(PDType1Font.HELVETICA_OBLIQUE, getStandardType1Font(StandardType1Font.HELVETICA_OBLIQUE));
        assertEquals(PDType1Font.TIMES_BOLD, getStandardType1Font(StandardType1Font.TIMES_BOLD));
        assertEquals(PDType1Font.TIMES_BOLD_ITALIC, getStandardType1Font(StandardType1Font.TIMES_BOLD_ITALIC));
        assertEquals(PDType1Font.TIMES_ITALIC, getStandardType1Font(StandardType1Font.TIMES_ITALIC));
        assertEquals(PDType1Font.TIMES_ROMAN, getStandardType1Font(StandardType1Font.TIMES_ROMAN));
        assertEquals(PDType1Font.SYMBOL, getStandardType1Font(StandardType1Font.SYMBOL));
        assertEquals(PDType1Font.ZAPF_DINGBATS, getStandardType1Font(StandardType1Font.ZAPFDINGBATS));
    }

    private PDFont findFontFor(String s) {
        try {
            return FontUtils.findFontFor(new PDDocument(), s);
        } finally {
            FontUtils.clearLoadedFontCache();
        }
    }

    @Test
    public void testCanDisplay() {
        assertTrue(canDisplay("Chuck", getStandardType1Font(StandardType1Font.HELVETICA)));
        assertFalse(canDisplay("?????", getStandardType1Font(StandardType1Font.HELVETICA)));
        assertFalse(canDisplay("Chuck", null));
    }

    @Test
    public void testFindFontFor() {
        assertNotNull(findFontFor("?????"));
        assertNotNull(findFontFor("???? ????? ??? ????"));
        assertNotNull(findFontFor("????"));
        assertNotNull(findFontFor("????"));
        assertNotNull(findFontFor("?"));
        assertNotNull(findFontFor("?"));
        assertNotNull(findFontFor("?????"));
        assertNotNull(findFontFor("???"));
        assertNotNull(findFontFor("???/???"));
        assertNotNull(findFontFor("???"));
        assertNotNull(findFontFor("latin ?????????"));
        assertNotNull(findFontFor("? ?"));
        assertNotNull(findFontFor("Newlines\nare\r\nignored"));
        assertNotNull(findFontFor("? ? ? ???? "));
        assertNotNull(findFontFor("?????????"));
        assertNotNull(findFontFor("?"));
        assertNotNull(findFontFor("??????, ??????"));
        assertNotNull(findFontFor("?????"));
        assertNotNull(findFontFor("???????"));
        assertNotNull(findFontFor("????????"));
        assertNotNull(findFontFor("???????"));
        assertNotNull(findFontFor("?????"));
        assertNotNull(findFontFor("??????"));
        assertNotNull(findFontFor("?????"));
        assertNotNull(findFontFor("??? ???"));
        assertNotNull(findFontFor("????? ????"));
    }

    @Test
    public void fontForMultipleLanguagesInOneString() {
        assertNotNull(findFontFor("???????? ????? ??????? ????? ??????, ?????? ????? ???? ?????????? latin ????????? ????? ??? ???? ?????? ? ? ? ?????????  ?????"));
    }

    @Test
    public void roundTripWriteAndRead() throws TaskException, IOException {
        List<String> strings = Arrays.asList("?????????", "???", "??????? ?????? ?????", "?????", "????????", "??? ?? ?? ???? ??? ??????", "123 ?????? ????", "032 ??? ?????? ??????", "This is ????  Mixed ?????? ????", "????? ???? ????????? latin ????????? ????? ??? ???? ?????? ? ? ? ???? ", "??? ?????? ??????");
        for (String str : strings) {
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            new PageTextWriter(doc).write(page, new Point(10, 10), str, getStandardType1Font(StandardType1Font.HELVETICA), 10.0d, Color.BLACK);
            doc.addPage(page);
            PDDocumentHandler handler = new PDDocumentHandler(doc);
            File tmp = IOUtils.createTemporaryBuffer();
            handler.savePDDocument(tmp);
            PDDocument doc2 = PDFParser.parse(SeekableSources.seekableSourceFrom(tmp));
            String text = new PdfTextExtractorByArea().extractTextFromArea(doc2.getPage(0), new Rectangle(0, 0, 1000, 1000));
            assertEquals(noWhitespace(str), noWhitespace(text));
        }
    }

    private String noWhitespace(String in) {
        return in.replaceAll("\\s", "");
    }

    @Test
    public void testFontOrFallbackPositive() {
        assertEquals(HELVETICA, fontOrFallback("Chuck", HELVETICA, new PDDocument()));
    }

    @Test
    public void testFontOrFallbackNegative() {
        assertNotNull(fontOrFallback("?????", HELVETICA, new PDDocument()));
    }

    @Test
    public void testFontOrFallbackNotFoundFallback() {
        assertNull(fontOrFallback("??????, ?????", HELVETICA, new PDDocument()));
    }

    @Test
    public void testCaching() {
        PDDocument doc = new PDDocument();
        PDFont expected = FontUtils.findFontFor(doc, "?????");
        assertNotNull(expected);
        PDFont actual = FontUtils.findFontFor(doc, "??");
        assertTrue("Font is cached, same instance is returned", expected == actual);
    }

    @Test
    public void testCanDisplayThai() {
        assertThat(findFontFor("??????????????"), is(notNullValue()));
    }

    @Test
    public void canDisplayGeorgian() {
        assertNotNull(findFontFor("??????? ???"));
    }

    @Test
    public void testCanDisplayType0FontsThatDontThrow() throws TaskIOException, IOException {
        PDDocument doc = getTestDoc("pdf/2-up-sample.pdf");
        PDResources res = doc.getPage(0).getResources();
        PDFormXObject form = (PDFormXObject) res.getXObject(COSName.getPDFName("Form2"));
        PDResources formRes = form.getResources();
        PDFont font = formRes.getFont(COSName.getPDFName("F0"));
        assertThat(font.getName(), is("Arial-BoldMT"));
        assertThat(FontUtils.canDisplay("Redacted out :)", font), is(false));
    }

    @Test
    public void testLoadingFullFontFromSystemForSubsetFonts() throws TaskIOException, IOException {
        boolean isVerdanaAvailable = isFontAvailableOnSystem("Verdana");
        if (!isVerdanaAvailable) {
            return;
        }
        PDDocument doc = getTestDoc("pdf/subset-font.pdf");
        PDResources res = doc.getPage(0).getResources();
        PDFormXObject form = (PDFormXObject) res.getXObject(COSName.getPDFName("Xf1"));
        PDResources formRes = form.getResources();
        PDFont font = formRes.getFont(COSName.getPDFName("F1"));
        assertThat(font.getName(), is("PXAAAA+Verdana"));
        PDFont original = new FontUtils.FontSubsetting(font).loadOriginal(doc);
        assertThat(original.getName(), is("Verdana"));
    }

    private PDDocument getTestDoc(String name) throws TaskIOException {
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader().getResourceAsStream(name), randomAlphanumeric(16) + ".pdf");
        return new DefaultPdfSourceOpener().open(source).getUnderlyingPDDocument();
    }

    private boolean isFontAvailableOnSystem(String name) {
        FontMapping<TrueTypeFont> result = FontMappers.instance().getTrueTypeFont(name, null);
        return result != null && !result.isFallback();
    }

    @Test
    public void resolveFontsWhenTextRepeats() throws TaskIOException {
        PDDocument doc = new PDDocument();
        List<TextWithFont> textAndFonts = FontUtils.resolveFonts("123?456?789", HELVETICA, doc);
        assertThat(textAndFonts.get(0).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(0).getText(), is("123"));
        assertThat(textAndFonts.get(1).getFont().getName(), is(not("Helvetica")));
        assertThat(textAndFonts.get(1).getText(), is("?"));
        assertThat(textAndFonts.get(2).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(2).getText(), is("456"));
        assertThat(textAndFonts.get(3).getFont().getName(), is(not("Helvetica")));
        assertThat(textAndFonts.get(3).getText(), is("?"));
    }

    @Test
    public void resolveFontsWhenSpaceSeparately() throws TaskIOException {
        PDDocument doc = new PDDocument();
        List<TextWithFont> textAndFonts = FontUtils.resolveFonts("ab cd", HELVETICA, doc);
        assertThat(textAndFonts.get(0).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(0).getText(), is("ab"));
        assertThat(textAndFonts.get(1).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(1).getText(), is(" "));
        assertThat(textAndFonts.get(2).getFont().getName(), is("Helvetica"));
        assertThat(textAndFonts.get(2).getText(), is("cd"));
    }

    @Test
    public void resolveFontsWhenUnsupportedCharacters() throws TaskIOException {
        PDDocument doc = new PDDocument();
        List<TextWithFont> textAndFonts = FontUtils.resolveFonts("ab?cd", HELVETICA, doc);
        assertThat(textAndFonts.get(1).getFont(), is(nullValue()));
        assertThat(textAndFonts.get(1).getText(), is("?"));
        assertThat(FontUtils.removeUnsupportedCharacters("ab ?cd", doc), is("ab cd"));
    }
}

