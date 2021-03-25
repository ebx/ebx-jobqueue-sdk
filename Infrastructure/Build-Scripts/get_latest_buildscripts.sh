#!/bin/bash
set -u
#
#  ECHOBOX CONFIDENTIAL
#
#  All Rights Reserved.
#
#  NOTICE: All information contained herein is, and remains the property of
#  Echobox Ltd. and itscheck_environment_selection suppliers, if any. The
#  intellectual and technical concepts contained herein are proprietary to
#  Echobox Ltd. and its suppliers and may be covered by Patents, patents in
#  process, and are protected by trade secret or copyright law. Dissemination
#  of this information or reproduction of this material, in any format, is
#  strictly forbidden unless prior written permission is obtained
#  from Echobox Ltd.
#
# This script will get the buildscripts zip and unzip into Buildscripts directory

#######################################
# Delete file if exists
# Globals:
# Arguments:
#  * par 1 : file to delete
# Returns:
#   None
#######################################
delete_file() {
  if [ -f "$1" ]; then
    rm "$1"
  fi
}

main() {
  local codebuild_src_dir=""
  local build_scripts_dir=""

  codebuild_src_dir=$(printenv CODEBUILD_SRC_DIR)
  build_scripts_dir=$(printenv BUILD_SCRIPTS_DIR)

  local unzip_destination="${codebuild_src_dir}/${build_scripts_dir}"
  local s3_file
  local local_zip_file="${codebuild_src_dir}/buildscripts.zip"

  delete_file "${local_zip_file}"

  ## get the value from SSM
  local ssm_param="/Infrastructure/Codebuild/DefaultBuildScriptsLocation"

  s3_file=$(aws ssm get-parameter --name "${ssm_param}" --query "Parameter.[Value]" --output text)

  ## now get this file
  echo "About to get ${s3_file}"
  aws s3 cp "${s3_file}" "${local_zip_file}"

  ## create unzip destination if does not exists
  echo "About to create unzip destination ${unzip_destination}"
  mkdir -p "${unzip_destination}"
  echo "About to unzip ${local_zip_file} to ${unzip_destination}"
  unzip -o "${local_zip_file}" -d "${unzip_destination}"
  echo "Here is list of files extracted .."
  ls -al "${unzip_destination}"

  ## ensure line ending correct
  echo "Ensure line endings correct"
  find "${unzip_destination}" -type f -print0 | xargs -0 dos2unix
  echo "All done"
}

main
