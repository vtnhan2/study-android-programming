import PyPDF2

try:
    with open('d:\\School\\HK8\\android\\Docs\\TH LAP TRINH DI DONG.pdf', 'rb') as file:
        reader = PyPDF2.PdfReader(file)
        text = ""
        with open('pdf_output.txt', 'w', encoding='utf-8') as out_file:
            for page_num in range(len(reader.pages)):
                out_file.write(f"--- PAGE {page_num + 1} ---\n")
                out_file.write(reader.pages[page_num].extract_text() + "\n")
        print("PDF extracted to pdf_output.txt")
except Exception as e:
    print(f"Error: {e}")
