package com.til;

import java.io.IOException;
import java.io.InputStream;

import org.mozilla.javascript.RhinoException;
/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/

/*import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.extensions.script.RhinoScriptBuilder;
import ro.isdc.wro.extensions.script.RhinoUtils;
import ro.isdc.wro.util.StopWatch;
import ro.isdc.wro.util.WroUtil;*/
import com.til.RhinoScriptBuilder;
import com.til.RhinoUtils;


public class UglifyJSUtil {

	private final boolean uglify = true;
	private static final String DEFAULT_UGLIFY_JS = "/resources/uglify-1.0.6.min.js";
	//private static final Logger logger = LoggerFactory.getLogger(UglifyJSUtil.class);

	public String processJS(final String code) throws IOException {
		try {
			//final StopWatch watch = new StopWatch();
			//watch.start("init");
			final RhinoScriptBuilder builder = initScriptBuilderJS();
			//watch.stop();
			//watch.start(uglify ? "uglify" : "beautify");

			//final String originalCode = WroUtil.toJSMultiLineString(code);
			final String originalCode = toJSMultiLineString(code);
			final StringBuffer sb = new StringBuffer("(function() {");
			sb.append("var orig_code = " + originalCode + ";");
			sb.append("var ast = jsp.parse(orig_code);");
			sb.append("ast = exports.ast_mangle(ast);");
			sb.append("ast = exports.ast_squeeze(ast);");
			// the second argument is true for uglify and false for beautify.
			sb.append("return exports.gen_code(ast, {beautify: " + !uglify
					+ " });");
			sb.append("})();");
			if (builder != null) {
				final Object result = builder.evaluate(sb.toString(),
						"uglifyIt");
				//watch.stop();
				//logger.debug(watch.prettyPrint());
				return String.valueOf(result);
			}
		} catch (final RhinoException e) {
			//throw new WroRuntimeException(RhinoUtils.createExceptionMessage(e),e);
			//throw new Exception(RhinoUtils.createExceptionMessage(e),e);
			System.out.println(e.getScriptStackTrace() + e.getMessage());

		} catch (Exception ex) {
			System.out.println(ex.getStackTrace() + ex.getMessage());
		}
		return "";
	}

	private RhinoScriptBuilder initScriptBuilderJS() {
		try {
			final String scriptInit = "var exports = {}; function require() {return exports;}; var process={version:0.1};";

			return RhinoScriptBuilder.newChain().addJSON()
					.evaluateChain(scriptInit, "initScript")
					.evaluateChain(getScriptAsStreamJS(), DEFAULT_UGLIFY_JS);
		} catch (final IOException ex) {
			throw new IllegalStateException("Failed initializing js", ex);
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error(ex.getStackTrace() + ex.getMessage());
		}
		return null;
	}

	protected InputStream getScriptAsStreamJS() {
		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getResourceAsStream("/resources/uglify-1.0.6.min.js");
			//inputStream = getClass().getResourceAsStream("E:\\gs-maven-master\\complete\\src\\main\\java\\resources\\uglify-1.0.6.min.js");
			if (inputStream == null) {
				System.out.println("inputStream null");
				//logger.debug("inputStream null");
				return inputStream;
			} 
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.error(ex.getStackTrace() + ex.getMessage());
		}
		return inputStream;

	}
	
	
	 public static String toJSMultiLineString(final String data) {
		    final StringBuffer result = new StringBuffer("[");
		    if (data != null) {
		      final String[] lines = data.split("\n");
		      if (lines.length == 0) {
		        result.append("\"\"");
		      }
		      for (int i = 0; i < lines.length; i++) {
		        final String line = lines[i];
		        result.append("\"");
		        result.append(line.replace("\\", "\\\\").replace("\"", "\\\"").replaceAll("\\r|\\n", ""));
		        // this is used to force a single line to have at least one new line (otherwise cssLint fails).
		        if (lines.length == 1) {
		          result.append("\\n");
		        }
		        result.append("\"");
		        if (i < lines.length - 1) {
		          result.append(",");
		        }
		      }
		    }
		    result.append("].join(\"\\n\")");
		    return result.toString();
		  }
	
	
	
}
