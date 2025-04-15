## Booru Mass Downloader

Cute little tool to download a ton of pictures from gelbooru-based websites all at once.

|   Example using pre-defined booru    |       Example using custom URL       |
|:------------------------------------:|:------------------------------------:|
| ![](https://i.imgur.com/c7uJeGR.png) | ![](https://i.imgur.com/ZWqFap9.png) |

### FAQ

#### How do I run this?

See the following [thread](https://github.com/ayaxperson/booru-mass-downloader/issues/1#issuecomment-2798764842).

#### I'm getting a 5XX error, what should I do?

Errors in the 500-599 range respond to server-side errors, therefore the issue is most likely caused by the website you're trying to access. In some cases, they can also mean the gelbooru instance you're trying to download from has blocked API access.

### Input fields

#### Booru URL

This should be the URL of the booru you're trying to download data from. If your input doesn't start with "http://" or "https://" the program will assume the HTTPS protocol should be used. You may also input the name of a booru website if it's in the [Boorus enum in gelbooru-java](https://github.com/ayaxperson/gelbooru-java/blob/master/src/main/java/me/ajax/gelbooru/Boorus.java).

#### Output directory

This should be the path to a directory (folder) where the program will save the files downloaded.

#### Starting + Ending page

The starting and ending page index of where to download from. For example, if 0 and 5 is inputted, the program will download everything on pages 0, 1, 2, 3, 4 and 5.

#### Original quality

If this is checked, the program will download the original quality pictures that you can access with the "Original image" option, rather than using the sample image. 

#### Overwrite existing files

If this is checked, the program will ignore files of the same name that are already in your output directory.

#### Tags

These are the tags the program's going to be looking up. You may seperate them with spaces or new lines.

### Footer

#### Status

This will change from its initial text once you first click the start button to show how many files you've downloaded and the sum size of them.

#### Warnings and errors

This shows how many errors were encountered and how many warnings were thrown. If the program fails to download a file, view a page, resolve the provided URL, ... it will be reported here. Warnings are more banal than errors and they may be thrown in case of, for example, encountering an existing file with the same name as one that was going to be downloaded. You may view explanation for these and errors and warnings using the console window.