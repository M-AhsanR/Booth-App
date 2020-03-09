package com.schopfen.Booth.ApiStructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.util.Args;

/**
 * Text body part backed by a byte array.
 *
 * @see org.apache.http.entity.mime.MultipartEntityBuilder
 *
 * @since 4.0
 */
public class StringBody extends AbstractContentBody {

    private final byte[] content;

    /**
     * @since 4.1
     *
     * @deprecated (4.3) use {@link org.apache.http.entity.mime.content.StringBody#StringBody(String, ContentType)}
     *   or {@link org.apache.http.entity.mime.MultipartEntityBuilder}
     */
    @Deprecated
    public static org.apache.http.entity.mime.content.StringBody create(
            final String text,
            final String mimeType,
            final Charset charset) throws IllegalArgumentException {
        try {
            return new org.apache.http.entity.mime.content.StringBody(text, mimeType, charset);
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Charset " + charset + " is not supported", ex);
        }
    }

    /**
     * @since 4.1
     *
     * @deprecated (4.3) use {@link org.apache.http.entity.mime.content.StringBody#StringBody(String, ContentType)}
     *   or {@link org.apache.http.entity.mime.MultipartEntityBuilder}
     */
    @Deprecated
    public static org.apache.http.entity.mime.content.StringBody create(
            final String text, final Charset charset) throws IllegalArgumentException {
        return create(text, null, charset);
    }

    /**
     * @since 4.1
     *
     * @deprecated (4.3) use {@link org.apache.http.entity.mime.content.StringBody#StringBody(String, ContentType)}
     *   or {@link org.apache.http.entity.mime.MultipartEntityBuilder}
     */
    @Deprecated
    public static org.apache.http.entity.mime.content.StringBody create(final String text) throws IllegalArgumentException {
        return create(text, null, null);
    }

    /**
     * Create a StringBody from the specified text, MIME type and character set.
     *
     * @param text to be used for the body, not {@code null}
     * @param mimeType the MIME type, not {@code null}
     * @param charset the character set, may be {@code null}, in which case the US-ASCII charset is used
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException if the {@code text} parameter is null
     *
     * @deprecated (4.3) use {@link org.apache.http.entity.mime.content.StringBody#StringBody(String, ContentType)}
     *   or {@link org.apache.http.entity.mime.MultipartEntityBuilder}
     */
    @Deprecated
    public StringBody(
            final String text,
            final String mimeType,
            final Charset charset) throws UnsupportedEncodingException {
        this(text, ContentType.create(mimeType, charset));
    }

    /**
     * Create a StringBody from the specified text and character set.
     * The MIME type is set to "text/plain".
     *
     * @param text to be used for the body, not {@code null}
     * @param charset the character set, may be {@code null}, in which case the US-ASCII charset is used
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException if the {@code text} parameter is null
     *
     * @deprecated (4.3) use {@link org.apache.http.entity.mime.content.StringBody#StringBody(String, ContentType)}
     *   or {@link org.apache.http.entity.mime.MultipartEntityBuilder}
     */
    @Deprecated
    public StringBody(final String text, final Charset charset) throws UnsupportedEncodingException {
        this(text, "text/plain", charset);
    }

    /**
     * Create a StringBody from the specified text.
     * The MIME type is set to "text/plain".
     * The {@linkplain Consts#ASCII ASCII} charset is used.
     *
     * @param text to be used for the body, not {@code null}
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException if the {@code text} parameter is null
     *
     * @deprecated (4.3) use {@link org.apache.http.entity.mime.content.StringBody#StringBody(String, ContentType)}
     *   or {@link org.apache.http.entity.mime.MultipartEntityBuilder}
     */
    @Deprecated
    public StringBody(final String text) throws UnsupportedEncodingException {
        this(text, "text/plain", Consts.UTF_8);
    }

    /**
     * @since 4.3
     */
    public StringBody(final String text, final ContentType contentType) {
        super(contentType);
        Args.notNull(text, "Text");
        final Charset charset = contentType.getCharset();
        final String csname = charset != null ? charset.name() : Consts.UTF_8.name();
        try {
            this.content = text.getBytes(csname);
        } catch (final UnsupportedEncodingException ex) {
            // Should never happen
            throw new UnsupportedCharsetException(csname);
        }
    }

    public Reader getReader() {
        final Charset charset = getContentType().getCharset();
        return new InputStreamReader(
                new ByteArrayInputStream(this.content),
                charset != null ? charset : Consts.UTF_8);
    }

    public void writeTo(final OutputStream out) throws IOException {
        Args.notNull(out, "Output stream");
        final InputStream in = new ByteArrayInputStream(this.content);
        final byte[] tmp = new byte[4096];
        int l;
        while ((l = in.read(tmp)) != -1) {
            out.write(tmp, 0, l);
        }
        out.flush();
    }

    public String getTransferEncoding() {
        return MIME.ENC_8BIT;
    }

    public long getContentLength() {
        return this.content.length;
    }

    public String getFilename() {
        return null;
    }

}