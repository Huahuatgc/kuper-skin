import zipfile
import os

path = r'd:\kuper\app\src\main\assets\wallpapers\Rate_UI_v17.klwp.zip'
if os.path.exists(path):
    with zipfile.ZipFile(path, 'r') as zip_ref:
        for name in zip_ref.namelist():
            print(name)
else:
    print(f"File not found: {path}")
    # List directory to see what's there
    print("Contents of d:\\kuper\\app\\src\\main\\assets\\wallpapers:")
    for f in os.listdir(r'd:\kuper\app\src\main\assets\wallpapers'):
        print(f)
