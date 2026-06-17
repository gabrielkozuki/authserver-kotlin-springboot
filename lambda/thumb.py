from os.path import splitext
from urllib.parse import unquote_plus
from PIL import Image
from io import BytesIO

SUPPORTED_EXTENSIONS = [".png", ".jpg", ".jpeg", ".gif", ".bmp", ".tiff", ".webp"]


def resize(src, size):
    with Image.open(src) as src_image:
        src_image.thumbnail(size)
        dest_image = BytesIO()
        src_image.save(dest_image, format="PNG")
        dest_image.seek(0)  # leitura a partir do inicio do arquivo
        return dest_image


# avatars/avatar.jpg, xl -> avatars/avatar_xl.png
def create_filename(path, prefix):
    parts = path.split("/")
    parts[-1] = f"{prefix}_{parts[-1]}.png"
    return "/".join(parts)


def find_records(event):
    if "Records" not in event:
        return []

    for record in event["Records"]:
        if "s3" not in record:
            continue

        bucket = record["s3"]["bucket"]["name"]
        key = unquote_plus(record["s3"]["object"]["key"])
        filename, ext = splitext(key)
        ext = ext.lower()

        if ext in SUPPORTED_EXTENSIONS:
            yield {"bucket": bucket, "key": key, "name": filename, "ext": ext}
