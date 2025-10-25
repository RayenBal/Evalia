import re, sys, pathlib, json
root = pathlib.Path('../EvaliaAPI/EvaliaProject/src/main/java')
out = []
javadoc_re = re.compile(r'/\*\*(?:\s*\*.*?)*?\*/', re.S)
class_re = re.compile(r'(public\s+)?(class|interface|enum)\s+([A-Za-z0-9_]+)')
method_re = re.compile(r'(public|private|protected)\s+[^\(]+\s+([A-Za-z0-9_]+)\s*\(')
mapping_re = re.compile(r'@(RequestMapping|GetMapping|PostMapping|PutMapping|DeleteMapping)\s*(\([^)]*\))?', re.S)
anno_path_re = re.compile(r'path\s*=\s*"(.*?)"|value\s*=\s*"(.*?)"|"\s*(/[^"]+)"')
entity_field_re = re.compile(r'(private|protected|public)\s+([A-Za-z0-9_<>, ?]+)\s+([A-Za-z0-9_]+)\s*;')
for p in root.rglob('*.java'):
    try:
        text = p.read_text(encoding='utf-8')
    except Exception:
        continue
    doc_matches = [(m.start(), m.group(0)) for m in javadoc_re.finditer(text)]
    classes = []
    for m in class_re.finditer(text):
        classes.append({'type': m.group(2), 'name': m.group(3), 'pos': m.start()})
    mappings = []
    for m in mapping_re.finditer(text):
        snippet = m.group(0)
        # extract path(s) within parentheses
        paren = m.group(2) or ""
        paths = re.findall(r'"(/[^"]*)"', paren)
        mappings.append({'annotation': m.group(1), 'paths': paths, 'pos': m.start()})
    methods = []
    for m in method_re.finditer(text):
        methods.append({'name': m.group(2), 'pos': m.start()})
    fields = []
    for m in entity_field_re.finditer(text):
        fields.append({'visibility': m.group(1), 'type': m.group(2).strip(), 'name': m.group(3)})
    # attempt to attach closest javadoc above class or method
    def find_javadoc_before(pos):
        jd = None
        for s, block in doc_matches:
            if s < pos:
                jd = block
            else:
                break
        return jd
    file_info = {
        'path': str(p.relative_to(root.parent)),
        'classes': [],
        'mappings': mappings,
        'fields': fields
    }
    for c in classes:
        jd = find_javadoc_before(c['pos'])
        file_info['classes'].append({'name': c['name'], 'type': c['type'], 'javadoc': jd})
    out.append(file_info)

# write JSON summary for programmatic use
with open('java_summary.json', 'w', encoding='utf-8') as fh:
    json.dump(out, fh, indent=2, ensure_ascii=False)

# also a human readable summary
with open('java_summary.txt', 'w', encoding='utf-8') as fh:
    for item in out:
        fh.write("==== " + item['path'] + " ====\n")
        for c in item['classes']:
            fh.write(f"Class: {c['name']} ({c['type']})\n")
            if c['javadoc']:
                fh.write("Javadoc:\n")
                fh.write(c['javadoc'] + "\n")
        if item['mappings']:
            fh.write("Mappings:\n")
            for m in item['mappings']:
                fh.write("  @" + m['annotation'] + " paths=" + str(m['paths']) + "\n")
        if item['fields']:
            fh.write("Fields:\n")
            for f in item['fields'][:20]:
                fh.write(f"  {f['visibility']} {f['type']} {f['name']}\n")
        fh.write("\n\n")
print("OK")
