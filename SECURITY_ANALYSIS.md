# Security Analysis Report - Logger++ v3.20.1

## Critical Vulnerabilities

1. **Log4j Outdated (CVE-2021-44832)**: Uses log4j-core 2.19.0 (build.gradle:25), vulnerable to RCE via JDBCAppender configuration write. **Exploit**: Attacker with config write access executes arbitrary JNDI lookup leading to RCE.

2. **XXE (XML External Entity)**: log4j2.xml:1 includes XInclude namespace without entity restrictions. **Exploit**: Malicious XML config enables local file disclosure (`<!ENTITY xxe SYSTEM "file:///etc/passwd">`), SSRF, or DoS.

3. **ReDoS (Regex DoS)**: GrepperController.java:106 executes user regex on large HTTP bodies without timeout. **Exploit**: Pattern like `(a+)+b` causes catastrophic backtracking, consuming 100% CPU and hanging threads indefinitely.

4. **Unbounded Thread Pool**: LogProcessor.java:68 creates pool with Integer.MAX_VALUE (2.1B) threads. **Exploit**: Flood with requests to exhaust memory (each thread ~1MB stack = OOM crash).

5. **CSV Injection**: CSVExporter.java:240-253 only prefixes formulas with quote, incomplete sanitization. **Exploit**: Inject `=cmd|'/c calc'!A1` to achieve RCE when victim opens CSV in Excel.

6. **Credential Disclosure**: ElasticExporter.java:142 logs usernames/API keys in INFO level. **Exploit**: Credentials exposed in Burp console, extension logs, and error output accessible to other extensions.

7. **Array Index OOB**: LoggerImport.java:97 accesses `v[3]` without bounds check on CSV split. **Exploit**: Malformed import file with <4 columns triggers ArrayIndexOutOfBoundsException, crashes import, potential memory corruption.

8. **Path Traversal**: File exporters (CSVExporter.java:45,179, HARExporter.java:37, JSONExporter.java:35) lack path canonicalization. **Exploit**: Symlink attack or directory traversal to overwrite `/etc/crontab` or `~/.ssh/authorized_keys`.

9. **Elasticsearch Index Injection**: ElasticExporter.java:120,193 uses unsanitized index name. **Exploit**: Wildcard index `*` bypasses access controls, enabling unauthorized data access across all indices.

10. **Base64 Decode Bomb**: Base64DecodeTransformer.java:16 decodes without size limit. **Exploit**: Small base64 payload expands to gigabytes (e.g., nested compression), triggers OOM and kills JVM.
