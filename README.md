# local-document-index
An implementation of local document index similar to elasticsearch, which can handle large number of documents and terms without using excessive memory or disk I/O.

The project can be separated in 4 parts: tokenizing, indexing, merging and retrieving.

## Tokenizing

### Dataset
  - This project uses trec_ap89 corpus that can be found from [TREC](http://trec.nist.gov/data.html).
  - The total size of the dataset is 284.1 MB.
  - Total number of documents is 84678.

### Clearning files
  - Use **Jsoup** to read the HTML like formated files from the dataset.
  - For each file, replace characters that matches the **regular expression** "`[.]+[^A-Za-z0-9]`" with "` `" and replace "`[.]+[^A-Za-z0-9]`" with "` `".
  - After that only words, numbers and terms like "127.0.0.0" are left.
  
### Tokenizing
  - Convert each term in a document to a (term_id, doc_id, position) tuple.
  - Store term and term_id in a catalogue file.
  
For example, in document with `doc_id 20`:
```
The car was in the car wash.
```

After tokenizing it is converted to :
```
(1, 20, 1), (2, 20, 2), (3, 20, 3), (4, 20, 4), (1, 20, 5), (2, 20, 6), (5, 20, 7)
```
  
In catalogue:
```
1: the 
2: car 
3: was 
4: in 
5: wash
```


### Stopping and stemming
  - Load this [stop list](stopwords.txt) in hash table and skip those words.
  - Use **PorterStemmer** from [Open NLP](https://opennlp.apache.org/) library to stem terms.


## Indexing
  - Record tokens from all documents and save them in the [termlist](index/termlist.txt) file with following format(df for document frequency, ttf for total term frequency, tf for term frequency):
```
term_id df ttf;doc_id1 tf position1 position2 ...;doc_id2 tf position1 posiont2 ...; ...
```
For example, `1 1 1;47407 1 258` means `term_id 1` appears once in the whole dataset and appears in only one document. In `document_id 47407` it appears once in position 258.
  - Record term_id, term and the offset in termlist for all terms to [catalogue](index/catalogue.txt) file.
  
For exapmle, `2 flashier 33 72` means term number 2 is "flashier", and it is store in position 33 with length 72 in the termlist file.

  - Record document name, document_id and document length in [docInfo](index/docInfo.txt) file.


## Merging

## Retrieving


### License

This project is under the [MIT license](LICENSE).
