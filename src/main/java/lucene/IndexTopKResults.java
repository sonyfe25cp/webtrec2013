package lucene;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import lemurproject.indri.ParsedDocument;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import edu.bit.dlde.extractor.BlockExtractor;
import edu.bit.dlde.extractor.NormalExtractor;
import edu.bit.dlde.extractor.SimpleHtmlExtractor;

/**
 * index the parsedDocument to lucene index
 * @author ChenJie
 *
 */
public class IndexTopKResults {

	private String indexPath = "/data/webtrec/index/";
	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
	IndexWriter iw = null;
	NIOFSDirectory dir=null;
	NormalExtractor ne=new NormalExtractor();
	BlockExtractor be=new BlockExtractor();
	SimpleHtmlExtractor she=new SimpleHtmlExtractor();
	public IndexTopKResults(){
		
		File index=new File(indexPath);
		if(!index.exists()){
			index.mkdirs();
		}
		try {
			dir = new NIOFSDirectory(index);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35,analyzer);
			conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
			iw = new IndexWriter(dir, conf);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * just for debug parse process
	 * @param pdocs
	 * @param qid
	 * Aug 2, 2012
	 */
	public void index_for_debug(List<ParsedDocument> pdocs,int qid) {
		for (ParsedDocument pdoc : pdocs) {
			IndexDocument iDoc=parseDocument(pdoc);
			if(iDoc==null){
				continue;
			}
		}
	}
	/**
	 * index all the doc
	 * @param pdocs
	 * @param qid
	 * Aug 2, 2012
	 */
	public void index(List<ParsedDocument> pdocs,int qid) {
		try {
			int count = iw.maxDoc()+1;
			Document doc = null;
			IndexDocument iDoc=null;
			for (ParsedDocument pdoc : pdocs) {
				
				iDoc=parseDocument(pdoc);
				if(iDoc==null){
					continue;
				}
				Field queryField = new Field("qid", qid+"", Store.YES,
						Index.NOT_ANALYZED);
				Field uniqueField = new Field("id", count + "", Store.YES,
						Index.NOT_ANALYZED);
				Field titleField = new Field("title", iDoc.getTitle(),
						Store.YES, Index.ANALYZED, TermVector.YES);
				Field bodyField = new Field("body", iDoc.getBody(), Store.YES,
						Index.ANALYZED, TermVector.YES);
				Field urlField = new Field("url", iDoc.getUrl(), Store.YES,
						Index.NOT_ANALYZED);
				Field anchorField = new Field("anchor", iDoc.getAnchor(), Store.YES,
						Index.ANALYZED, TermVector.YES);

				doc = new Document();
				doc.add(uniqueField);//标示不同的document
				doc.add(queryField);//标示不同的query
				doc.add(titleField);
				doc.add(bodyField);
				doc.add(urlField);
				doc.add(anchorField);
				
				iw.addDocument(doc);
				count++;
			}
			iw.commit();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	public void close(){
		try {
			iw.close();
			dir.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (iw != null) {
				try {
					iw.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private IndexDocument parseDocument(ParsedDocument pdoc){
		String originBody=pdoc.content;
		String body="";
		String title_bak="";
		Reader reader=new StringReader(originBody);
		be.setReader(reader);
		try {
			be.extract();
			body = be.getContent();
			title_bak = be.getTitle();
			if (body == null || body.length() == 0) {
				return null;
			}
		} catch (Exception ie) {
			byte[] urls_tmp = (byte[]) (pdoc.metadata.get("url"));
			String url_tmp = new String(urls_tmp);
			System.out.println("error parse url : " + url_tmp);
			String fileName = "/data/webtrec/errorPages/"
					+ System.currentTimeMillis() + ".html";
			File file = new File(fileName);
			try {
				FileUtils.write(file, originBody);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// return null;
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		
		IndexDocument doc=new IndexDocument();

		byte[] titles = (byte[]) (pdoc.metadata.get("title"));
		String title="";
		if(titles!=null){
			title=new String(titles);
		}else{
			title=title_bak;
		}
		
		byte[] urls = (byte[]) (pdoc.metadata.get("url"));
		String url=new String(urls);
		
		
		String anchor= ne.getAnchorFromHtml(originBody);
		
		doc.setAnchor(anchor);
		doc.setBody(body);
		doc.setTitle(title);
		doc.setUrl(url);
		
		return doc;
	}
	
	
	
}
class IndexDocument{
	private String title;
	private String anchor;
	private String body;
	private String url;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAnchor() {
		return anchor;
	}
	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
