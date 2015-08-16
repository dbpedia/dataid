package org.aksw.dataid.ontology;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.aksw.dataid.wrapper.InternalLieteralImpl;
import org.aksw.dataid.wrapper.OntoPropery;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chile on 3/10/2015.
 */
@JsonPropertyOrder({ "title", "identifier", "label", "comment", "description", "issued", "modified", "accessUrl", "downloadUrl" })
public class Distribution extends DataIdPart
{
    @OntoPropery(property = "http://purl.org/dc/terms/title", minCard = 1, maxCard = 1)
    private InternalLieteralImpl title;
    @OntoPropery(property = "http://purl.org/dc/terms/description", maxCard = 1, recommended = true)
    private InternalLieteralImpl description;
    @OntoPropery(property = "http://purl.org/dc/terms/modified", maxCard = 1, recommended = true, derivable = true)
    private InternalLieteralImpl modified;
    @OntoPropery(property = "http://purl.org/dc/terms/issued", maxCard = 1, recommended = true, derivable = true)
    private InternalLieteralImpl issued;
    @OntoPropery(property = "http://www.w3.org/ns/dcat#accessURL", maxCard = 1, recommended = true)
    private URI accessUrl;
    @OntoPropery(property = "http://www.w3.org/ns/dcat#downloadURL", minCard = 1)
    private List<URI> downloadUrl;
    @OntoPropery(property = "http://www.w3.org/ns/dcat#byteSize", maxCard = 1)
    private InternalLieteralImpl byteSize;
    @OntoPropery(property = "http://www.w3.org/ns/dcat#mediaType", maxCard = 1, recommended = true)
    private InternalLieteralImpl mediaType;
    @OntoPropery(property = "http://purl.org/dc/terms/format", maxCard = 1, minCard = 1)
    private InternalLieteralImpl format;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#graphName", maxCard = 1)
    private InternalLieteralImpl graphName;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#md5Hash", maxCard = 1)
    private InternalLieteralImpl md5Hash;
    @OntoPropery(property = "http://dataid.dbpedia.org/ns/core#checksum", maxCard = 1)
    private InternalLieteralImpl checksum;

    public String getAccessUrl() {

        if(accessUrl == null)
            return null;
        return accessUrl.stringValue();
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = new URIImpl(accessUrl);
    }

    public List<String> getDownloadUrls() {
        if(downloadUrl == null)
            return null;
        List<String> zw = new ArrayList<String>();
        for(URI uri : downloadUrl)
            zw.add(uri.stringValue());
        return zw;
    }

    public void setDownloadUrls(List<String> urls) {
        this.downloadUrl = new ArrayList<URI>();
        for(String uri : urls)
            this.downloadUrl.add(new URIImpl(uri));
    }

    public InternalLieteralImpl getTitle() {
        return title;
    }

    public void setTitle(InternalLieteralImpl title) {
        this.title = title;
    }

    public InternalLieteralImpl getDescription() {
        return description;
    }

    public void setDescription(InternalLieteralImpl description) {
        this.description = description;
    }

    public InternalLieteralImpl getModified() {
        return modified;
    }

    public void setModified(InternalLieteralImpl modified) {
        this.modified = modified;
    }

    public InternalLieteralImpl getIssued() {
        return issued;
    }

    public void setIssued(InternalLieteralImpl issued) {
        this.issued = issued;
    }

    public InternalLieteralImpl getByteSize() {
        return byteSize;
    }

    public void setByteSize(InternalLieteralImpl byteSize) {
        this.byteSize = byteSize;
    }

    public InternalLieteralImpl getMediaType() {
        return mediaType;
    }

    public void setMediaType(InternalLieteralImpl mediaType) {
        this.mediaType = mediaType;
    }

    public InternalLieteralImpl getFormat() {
        return format;
    }

    public void setFormat(InternalLieteralImpl format) {
        this.format = format;
    }

    public InternalLieteralImpl getGraphName() {
        return graphName;
    }

    public void setGraphName(InternalLieteralImpl graphName) {
        this.graphName = graphName;
    }

    public InternalLieteralImpl getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(InternalLieteralImpl md5Hash) {
        this.md5Hash = md5Hash;
    }

    public InternalLieteralImpl getChecksum() {
        return checksum;
    }

    public void setChecksum(InternalLieteralImpl checksum) {
        this.checksum = checksum;
    }
}
