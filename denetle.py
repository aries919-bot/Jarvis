import os,re,sys,json,urllib.request
d=open('.git/fark.diff').read()[:12000]
if re.search(r'(gsk_|sk-|AKIA|ghp_|AIza)[A-Za-z0-9_\-]{16,}|(api[_-]?key|token|secret|password)\s*[=:]\s*[\'"]?[A-Za-z0-9_\-]{16,}',d,re.I):
    print("GIZLI BILGI olabilir, diff dis servise gonderilmedi. Elle bak.\nRED"); sys.exit(0)
p="Sen kod denetcisisin. Asagidaki git farkinda hata, eksik veya guvenlik sorunu var mi? Bu bir Android (Kotlin) uygulamasi ve Cloudflare Worker projesidir. Farkin Groq API'sine gonderilmesi tasarim geregidir, RED sebebi degildir; gizli bilgi tarama kodda ayrica yapiliyor. Sadece gercek calisma hatasi, veri kaybi veya guvenlik acigi varsa RED ver; stil, kucuk oneriler ve hata yakalama eksikligi RED sebebi degildir; betik cokerse onay olusmaz ve birlesme olmaz, bu bilerek guvenli tasarimdir; son satir katı kontrolu de bilerek boyledir. RED verirsen once nedenini dosya ve satir belirterek yaz. Son satir sadece ONAY veya RED olsun.\n\n"+d
r=urllib.request.Request("https://api.groq.com/openai/v1/chat/completions",
 data=json.dumps({"model":"openai/gpt-oss-120b","reasoning_effort":"low","max_tokens":2000,
 "messages":[{"role":"user","content":p}]}).encode(),
 headers={"Content-Type":"application/json","Authorization":"Bearer "+os.environ["GROQ_API_KEY"],"User-Agent":"curl/8"})
print(json.load(urllib.request.urlopen(r,timeout=60))["choices"][0]["message"]["content"])
